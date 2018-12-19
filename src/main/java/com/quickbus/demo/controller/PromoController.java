package com.quickbus.demo.controller;


import com.quickbus.demo.dao.GeneratePromoDao;
import com.quickbus.demo.dao.Impl.ValidatePromoCodeDaoImpl;
import com.quickbus.demo.dao.activatePromoDao;
import com.quickbus.demo.entity.PromoCodesEntity;
import com.quickbus.demo.model.*;
import com.quickbus.demo.repo.PromoCodesRepo;
import com.vividsolutions.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(path = "/promo")
public class PromoController {

    @Autowired
    GeneratePromoDao generatePromoDao;

    @Autowired
    activatePromoDao activatePromoDao;

    @Autowired
    PromoCodesRepo promoCodesRepo;

    @Autowired
    ValidatePromoCodeDaoImpl validatePromoCodeDao;

    @GetMapping(value = "/generate/{amount}")
    @ResponseBody
    public GenerateCodesModel generatePromo(@PathVariable int amount){
        return new GenerateCodesModel(200,generatePromoDao.generateCodes(amount));
    }

    @GetMapping(value = "/activate")
    @ResponseBody
    public ResponseModel activateAndDeactivatePromo(@RequestBody ActivateDeactivateModel activateDeactivateModel){
        String desc = "";

        if(activatePromoDao.activateAndDeactivate(activateDeactivateModel)){
            desc = "Promo Code "+activateDeactivateModel.getReq()+" is "+activateDeactivateModel.getRequestType();
            return new ResponseModel(200,desc);
        }
        desc = "Promo Code "+activateDeactivateModel.getReq()+" failed to "+activateDeactivateModel.getRequestType();
        return new ResponseModel(403,desc);
    }

    @GetMapping(value = "/activate/all")
    @ResponseBody
    public PromoCodesModel getAllActiveCode(){
        List<PromoCodesEntity> promoCodesEntity = promoCodesRepo.findByStatus(true);
        return new PromoCodesModel(200,promoCodesEntity);
    }

    @GetMapping(value = "/all")
    @ResponseBody
    public PromoCodesModel getAllCode(){
        List<PromoCodesEntity> promoCodesEntity = promoCodesRepo.findAll();
        return new PromoCodesModel(200,promoCodesEntity);
    }

    @PostMapping(value = "/setDestination")
    @ResponseBody
    public Object getValidation(@RequestBody ValidityModel validityModel){
        if(!validatePromoCodeDao.findDestination(validityModel)){
            return new ResponseModel(400,"Wrong Destination try Again");
        }
        Geometry geometry = validatePromoCodeDao.createLine(2.0,2.0);
        return new ResponseValidityModel(200,promoCodesRepo.findByPromocodes(validityModel.getPromocode()).get(),geometry.toText());

    }
    
}
