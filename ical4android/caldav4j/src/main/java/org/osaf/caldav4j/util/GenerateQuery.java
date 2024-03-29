package org.osaf.caldav4j.util;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern; 
import java.util.regex.Matcher; 

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;

import org.osaf.caldav4j.CalDAV4JException;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.DOMValidationException;
import org.osaf.caldav4j.model.request.CalDAVProp;
import org.osaf.caldav4j.model.request.CalendarData;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.model.request.Comp;
import org.osaf.caldav4j.model.request.CompFilter;
import org.osaf.caldav4j.model.request.Prop;
import org.osaf.caldav4j.model.request.PropFilter;
import org.osaf.caldav4j.model.request.TimeRange;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;



/** 
 * 
 * Copyright 2008 Roberto Polli
 * this class is an helper for creating Calendar-Query REPORTs
 * 
 * because of the complexity of iCalendar object and relative queries,
 * this class is intended to help the creation of basic queries like
 * get all VEVENT with THOSE attributes in THIS time-range
 * 
 * main schema is a strongly typed class, 
 *  with helpers/parsers that will create 
 *  the caldav query 
 * 
 * usage:
 * 
 * QueryGenerator qg = new QueryGenerator();
 * qg.setComponent("VEVENT"); // retrieve the whole VEVENT
 * qg.setComponent("VEVENT : UID, ATTENDEE, DTSTART, DTEND"); // retrieve the given properties
 * 
 * qg.setFilter("VEVENT"); //request on VEVENT
 * qg.setFilter("VEVENT [start;end] : UID==value1 , DTSTART==[start,end], DESCRIPTION==UNDEF, SUMMARY!=not my summary,")
 * 
 * start and end values can be empty strings "" or RFC2445-UTC timestamp
 */

/**
 * This Class is an helper for creating CalDAV queries.
 * @since 0.5
 * @experimental this class is experimental
 */
public class GenerateQuery  {
	
	// constants
	private static final String caldavNameSpaceQualifier = "C";
	
	// component attributes
	String requestedComponent = null; // VEVENT, VTODO
	List<String> requestedComponentProperties = new ArrayList<String>(); // a list of properties to be retrieved 
		

	// TODO how can I manage nested object queries?
	String filterComponent = null; // VEVENT, VTODO
	List<String> filterComponentProperties = new ArrayList<String>();
	Date timeRangeStart = null;
	Date timeRangeEnd = null;
	boolean noCalendarData = false;
	public void setNoCalendarData(boolean p) {
		this.noCalendarData = p;
	}
	
	// other settings: collation
	String collation = null; // use TextMatch default value
	// comp flags
	//TODO limit-recurrence-set, limit-freebusy-set, get-etag	

	private Date recurrenceSetEnd;

	private Date recurrenceSetStart;
	
	/**
	 * create a GenerateQuery object with the given parameters
	 * @param comp
	 * @param filter 
	 * 
	 * component syntax:
	 *  COMPONENT : PROP1,PROP2,..,PROPn
	 * filter syntax:
	 *  COMPONENT : PROP1==VALUE1,PROP2=VALUE2
	 * @throws ParseException 
	 * @throws ParseException 
	 */
	public GenerateQuery(String component, String filterComponent) 
	  throws CalDAV4JException {
		setComponent(component);
		setFilter(filterComponent);
	}
	
	/**
	 * 
	 */	
	public GenerateQuery() {
		
	}
	
	/**  
	 * constructor with Comp & CompFilter 
	 * 
	 *  XXX too low-level 
	 *  
	 */
	protected GenerateQuery(String c, List<String> cProp, 
			String cFilter, List<String> pFilter) {
		this(c, cProp, cFilter, pFilter, null);		
	}
	
	/**  
	 * constructor with Comp & CompFilter & collation  
	 */
	protected GenerateQuery(String c, List<String> cProp, 
			String cFilter, List<String> pFilter, String collation) {
		
		setComponent(c, cProp);
		setFilter(cFilter, pFilter);
		this.collation = collation;
	}
	
	/**  
	 * constructor with Comp & CompFilter as arrays 
	 * @deprecated 
	 */
	private GenerateQuery(String c, String cProp[], 
			String cFilter, String pFilter[]) {
		
		this(c, cProp != null ? Arrays.asList(cProp) : null,
				cFilter, pFilter != null ? Arrays.asList(pFilter) : null);
		
	}

	/**
	 * validator
	 * TODO this method should provide at least a basic validation of the calendar-query
	 * @deprecated This method does nothing but returning true!!!
	 */
	public boolean validate() {
		return true;
	}
	
	/**
	 * set the component to retrieve: VEVENT, VTODO
	 * VEVENT : UID, DTSTART, DTEND,
	 * 
	 */
	public void setComponent(String component) {
		if (component != null) {
			String cl[] = null;
			String c[] = component.trim().split("\\s*:\\s*",2);
			
			setRequestedComponent(c[0]);
			
			if (c.length>1){
				cl = c[1].trim().split("\\s*,\\s*");
				this.requestedComponentProperties =  Arrays.asList(cl);
			}					
		}
	}
	
	/**
	 * set the component and properties to retrieve: VEVENT, VTODO
	 */
	public void setComponent(String component, List<String> props) {
		if (component != null) {
			setRequestedComponent(component);
			this.requestedComponentProperties = props;
		}
	}
	
	/**
	 * transform the requestedComponentProperties fields in a PropComp value 
	 */
	private Comp getComp() {
		Comp vCalendarComp = new Comp("C");
		vCalendarComp.setName(Calendar.VCALENDAR);
		
		if (requestedComponent != null) {
			Comp vEventComp = new Comp("C");
			vEventComp.setName(requestedComponent);
			
			for (String propertyName : requestedComponentProperties ) {
				// add properties to VCALENDAR.VEVENT
				vEventComp.addProp(new CalDAVProp("C", "name", propertyName, false, false)); // @see modification to CalDAVProp
			}
			// add only one component...maybe more ;)
			List <Comp> comps = new ArrayList<Comp> ();			

			try {				
				vEventComp.validate();
				comps.add(vEventComp);
			} catch (DOMValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			vCalendarComp.setComps(comps);

		}
		return vCalendarComp;
	}
	
	
	/**
	 * set the component to filter: VEVENT, VTODO
	 * syntax:
	 * VTODO [;] : UID==1231423423145231 , DTSTART=[1214313254324;$3214234231] , SUMMARY!=Caldav4j
	 * @throws ParseException 
	 */
	public void setFilter(String filterComponent) 
	  throws CalDAV4JException  {		
		if (filterComponent != null) {
			String c[] = filterComponent.split("\\s*:\\s*",2); // split string in two
			Pattern compFilterString = Pattern.compile("(.+?)\\s*(\\[(.*?);(.*?)\\])?");

			
			Matcher m = compFilterString.matcher(c[0]);		
			if (m.matches()) {			
				setFilterComponent(m.group(1));
				
				// a time-range filter
				if (m.group(4) != null) {
					timeRangeStart = parseTime(m.group(3));
					timeRangeEnd = parseTime(m.group(4));					
				}
				
				if (c.length>1){
					String cl[] = c[1].trim().split("\\s*,\\s*");
					this.filterComponentProperties = Arrays.asList(cl);
				}
			}
		}			
	}
	
	
	/**
	 * set the component and properties to filter: VEVENT, VTODO
	 */
	public void setFilter(String filterComponent, List<String> props) {
		if (filterComponent != null) {
			setFilterComponent(filterComponent);
			setFilterComponentProperties(props);
		}
	}	
	
	/** 
	 * transform filterComponentProperties in a List of PropFilter
	 * this method parses 
	 * @throws ParseException 
	 */ 
	private List<PropFilter> getPropFilters() 
	throws CalDAV4JException {
		List<PropFilter> pf = new ArrayList<PropFilter>();
		Pattern filter = Pattern.compile("(.+?)([!=]=)(\\[(.*?);(.*?)\\]|([^\\]].+))");


		
		for (String p : this.filterComponentProperties) {
			String name = null;
			Boolean isDefined = null;
			boolean negateCondition = false;
			Date timeRangeStart = null, timeRangeEnd = null; 
			Boolean  textmatchcaseless = true;
			String textmatchString = null;
			
			//
			// parse: UID==3oij312po3214432 , DESCRIPTION!=Spada , DTSTART==[b;e]
			//
			Matcher str = filter.matcher(p);
			
			if (str.matches() && (str.group(3) != null ) ) {
				name = str.group(1);
				negateCondition = "!=".equals(str.group(2));				
				
				if (str.group(4) == null ) {
					// standard filter
					if ("UNDEF".equals(str.group(3))) {
						isDefined = false;
					} else {
						textmatchString = str.group(3);						
					}					
				} else if (str.group(5) != null ) {
					// a time-range filter
					timeRangeStart = parseTime(str.group(4));
					timeRangeEnd = parseTime(str.group(5));						
				}
				
				List<String> componentList = Arrays.asList(new String[] {
						Component.VALARM, Component.VEVENT, Component.VFREEBUSY, Component.VJOURNAL, Component.VTIMEZONE, Component.VTODO, Component.VVENUE }); 
				
				if (! componentList.contains(name)) {
					pf.add(new PropFilter(caldavNameSpaceQualifier, name, isDefined,
							timeRangeStart, timeRangeEnd, 
							textmatchcaseless, negateCondition, this.collation, textmatchString,  null));			
				} else {
					// if there, filter is invalid: we needed a comp-filter, not prop-filter
				}
			} else {
					// not a valid filter
			}

			
		}
		return pf;
	}
	
	/** 
	 * transform filters in a CompFilter
	 * @throws ParseException 
	 */ 
	private CompFilter getFilter() 
			throws CalDAV4JException {
		
		// search for VCALENDAR matching...
		CompFilter vCalendarCompFilter = new CompFilter("C");
		vCalendarCompFilter.setName(Calendar.VCALENDAR);

		// parse filterComponent
		if (this.filterComponent != null ) {
			CompFilter vEventCompFilter = new CompFilter("C", this.filterComponent,
					false, timeRangeStart, timeRangeEnd,												/// isDefined, dateStart, dateEnd
					null,getPropFilters().size()==0 ? null : getPropFilters());
			try {
				vEventCompFilter.validate();
				vCalendarCompFilter.addCompFilter(vEventCompFilter);
			} catch (DOMValidationException e) {
				// if filter is bad, don't add nothing
				e.printStackTrace();
			}
		}
				
		return vCalendarCompFilter;
	}
	
	/**
	 * this should parse QueryGenerator attributes
	 * and create the CalendarQuery
	 * @param recurrenceSetStart 
	 * @throws CalDAV4JException 
	 * @throws ParseException 
	 */
	public CalendarQuery generateQuery() 
		throws  CalDAV4JException {

		CalendarQuery query = new CalendarQuery("C", "D");				
		query.addProperty(CalDAVConstants.PROP_ETAG);

		if (!noCalendarData) {
			// TODO limit-recurrence-set
			CalendarData calendarData = new CalendarData("C");
			calendarData.setRecurrenceSetStart(recurrenceSetStart);
			calendarData.setRecurrenceSetEnd(recurrenceSetEnd);
			calendarData.setComp(getComp());
			
			query.setCalendarDataProp(calendarData);			
		}
		query.setCompFilter(getFilter());
		query.validate();
		
		return query;
	}

    public String prettyPrint() {
		//query.validate();			
	    try {
	    	Document doc = generateQuery().createNewDocument(XMLUtils
	                .getDOMImplementation());
			return XMLUtils.toPrettyXML(doc);
	    	
	    } catch (DOMValidationException domve) {
	        throw new RuntimeException(domve);
	    } catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
		
	}

    // getters+setters:
    //  remove trailing spaces from queries and get better input
    public void setRequestedComponent(String c) {
    	if (c != null) {
    		this.requestedComponent = c.trim();
    	}
    }
    
    public void setFilterComponent(String c) {
    	if (c != null) {
    		this.filterComponent = c.trim();
    	}
    }
    
    // if passed variable is null, a new ArrayList<String> remains
    public void setFilterComponentProperties(List<String> a) {
    	if (a != null) {
    		this.filterComponentProperties = a;
    	}
    }

	public void setRequestedComponentProperties(
			List<String> requestedComponentProperties) {
		if(requestedComponentProperties != null) {
			this.requestedComponentProperties = requestedComponentProperties;
		}
	}

	
	public void setTimeRange(Date start, Date end) {
		this.timeRangeStart = start;
		this.timeRangeEnd = end;
	}
	
	
	/**
	 * return the xml query
	 * @param query
	 * @return
	 * @throws DOMValidationException
	 */
	public static String printQuery(CalendarQuery query)
		throws DOMValidationException
	{	
				try {
					query.validate();
					Document doc = query.createNewDocument(XMLUtils
					        .getDOMImplementation());
					return XMLUtils.toPrettyXML(doc);

				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new DOMValidationException(e.getMessage());
				} 	        
	}
	
	/**
	 * parses a string to a Date using the following syntax:
	 *  - null or "" 	to 	null
	 *  - parsable		to Date(parsable)
	 *  - NOW			to Date(true) UTC
	 * @param time
	 * @return Date(time)
	 * @throws ParseException 
	 */
	private Date parseTime(String time) throws CalDAV4JException {
		if (time != null && !"".equals(time)) {
			if ("NOW".equals(time)) {
				return  new DateTime(true);
			} else {
				try {
					if (time.length()>8) {
						return new DateTime(time);	
					} else {
						return new Date(time);
					}
					
				} catch (ParseException e) {
					e.printStackTrace();
					throw new CalDAV4JException("Unparsable date format in query:"+time, e);

				}
			}
		}
				
		return null;
	}
    
    
    
}
// 
