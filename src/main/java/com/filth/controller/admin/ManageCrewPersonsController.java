package com.filth.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.filth.annotation.SkipInterceptor;
import com.filth.interceptor.BackgroundImageInterceptor;
import com.filth.link.Link;
import com.filth.link.ManageCrewPersonsLinkGenerator;
import com.filth.model.CrewPerson;
import com.filth.service.CrewPersonService;
import com.filth.util.GeneralUtil;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManageCrewPersonsController extends ManageEntityController implements ManageCrewPersonsLinkGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageCrewPersonsController.class);
    
    private static final String ENTITY_NAME = "CrewPerson";
    
    @Resource
    private CrewPersonService _crewPersonService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String CREW_PERSONS = ADMIN_URL_PREFIX + "/crewPersons";
        public static final String SAVE = CREW_PERSONS + "/save";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String MIDDLE_NAME = "middleName";
        public static final String POSITION_KNOWN_AS = "positionKnownAs";
        
    }
    
    private static final class ModelKey {
        public static final String CREW_PERSONS = "crewPersons";
        public static final String CREW_PERSON = "crewPerson";
    }

    @Override
    public Link getLinkToManageCrewPersons() {
        return new Link(URL.CREW_PERSONS);
    }

    @Override
    public Link getLinkToSaveCrewPerson() {
        return new Link(URL.SAVE);
    }
    
    @RequestMapping(value=URL.CREW_PERSONS, method=RequestMethod.GET)
    public ModelAndView manageCrewPersons() {
        List<CrewPerson> crewPersons = _crewPersonService.getAllCrewPersons();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.CREW_PERSONS, crewPersons);

        return new ModelAndView(ADMIN_VIEW_PREFIX + "/manage_crew_persons", mm);
    }
    
    @SkipInterceptor({BackgroundImageInterceptor.class})
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView saveCrewPerson(
            @RequestParam(value=URLParam.LAST_NAME) String lastName,
            @RequestParam(value=URLParam.POSITION_KNOWN_AS) String positionKnownAs,
            @RequestParam(value=URLParam.FIRST_NAME, required=false) String firstName,
            @RequestParam(value=URLParam.MIDDLE_NAME, required=false) String middleName,
            @RequestParam(value=URLParam.ID, required=false) Integer id) throws Exception {
        CrewPerson crewPerson = null;
        String fullName = GeneralUtil.buildFullName(firstName, middleName, lastName);
        
        try {
            if (null != id) {
                crewPerson = _crewPersonService.getCrewPersonById(id);
            } else {
                crewPerson = new CrewPerson();
            }
            
            crewPerson.setLastName(lastName);
            crewPerson.setFullName(fullName);
            crewPerson.setPositionKnownAs(positionKnownAs);
            
            //these else blocks below are counter-intuitive, but exist for a reason:
            //this may be an edit--such as removing a middle name. It's possible
            //that the param for the name is not null, but an empty string; in this
            //case we would want null instead.
            if (StringUtils.isNotEmpty(firstName)) {
                crewPerson.setFirstName(firstName);
            } else {
                crewPerson.setFirstName(null);
            }
            if (StringUtils.isNotEmpty(middleName)) {
                crewPerson.setMiddleName(middleName);
            } else {
                crewPerson.setMiddleName(null);
            }
            
            _crewPersonService.saveCrewPerson(crewPerson);
        } catch (Exception e) {
            LOGGER.error(String.format(SAVE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, fullName), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, fullName), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.CREW_PERSON, crewPerson);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, fullName), mm);
    }

}
