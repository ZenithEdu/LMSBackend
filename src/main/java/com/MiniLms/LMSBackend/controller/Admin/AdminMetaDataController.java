package com.MiniLms.LMSBackend.controller.Admin;

import com.MiniLms.LMSBackend.service.BatchService.IBatchService;
import com.MiniLms.LMSBackend.service.UserService.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/adminMetaData")
public class AdminMetaDataController {

    private final IBatchService batchService;
    private final IUserService userService;


    @Autowired
    public AdminMetaDataController(
        IBatchService batchService,
        IUserService userService
    ){
        this.batchService = batchService;
        this.userService = userService;
    }




}
