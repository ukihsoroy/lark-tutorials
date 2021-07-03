package io.ukihsoroy.lark.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p></p>
 *
 * @author K.O
 * @email ko.shen@hotmail.com
 */
@RestController
public class LoginController {


    @GetMapping("login/code")
    public String loginCode(@RequestParam String code, @RequestParam String state) {
        System.out.println(code);
        System.out.println(state);
        return code;
    }



}
