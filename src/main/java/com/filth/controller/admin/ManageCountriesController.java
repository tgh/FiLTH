package com.filth.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.filth.link.Link;
import com.filth.link.ManageCountriesLinkGenerator;
import com.filth.model.Country;
import com.filth.service.CountryService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManageCountriesController extends ManageEntityController implements ManageCountriesLinkGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageCountriesController.class);
    
    private static final String ENTITY_NAME = "Country";
    
    @Resource
    private CountryService _countryService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    
    private static final class URL {
        public static final String COUNTRIES = ADMIN_URL_PREFIX + "/countries";
        public static final String SAVE = COUNTRIES + "/save";
    }
    
    private static final class URLParam {
        public static final String ID = "id";
        public static final String NAME = "name";
    }
    
    private static final class ModelKey {
        public static final String COUNTRIES = "countries";
        public static final String COUNTRY = "country";
    }

    @Override
    public Link getLinkToManageCountries() {
        return new Link(URL.COUNTRIES);
    }
    
    @Override
    public Link getLinkToSaveCountry() {
        return new Link(URL.SAVE);
    }

    @RequestMapping(value=URL.COUNTRIES, method=RequestMethod.GET)
    public ModelAndView manageCountries() {
        List<Country> countries = _countryService.getAllCountries();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.COUNTRIES, countries);

        return new ModelAndView(ADMIN_VIEW_PREFIX + "/manage_countries", mm);
    }
    
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST)
    public ModelAndView saveCountry(
            @RequestParam(value=URLParam.ID, required=false) Integer id,
            @RequestParam(value=URLParam.NAME) String name) throws Exception {
        Country country = null;
        
        try {
            if (null != id) {
                country = _countryService.getCountry(id);
            } else {
                country = new Country();
            }
            
            country.setName(name);
            _countryService.saveCountry(country);
        } catch (Exception e) {
            LOGGER.error("An error occurred attempting to save " + ENTITY_NAME + "'" + name + "'", e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, name), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.COUNTRY, country);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, name), mm);
    }
    
}
