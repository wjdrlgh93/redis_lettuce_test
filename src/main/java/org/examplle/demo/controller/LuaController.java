package org.examplle.demo.controller;

import org.examplle.demo.service.LuaScriptingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LuaController {

    private final LuaScriptingService luaScriptingService;

    public LuaController(LuaScriptingService luaScriptingService) {
        this.luaScriptingService = luaScriptingService;
    }

    @GetMapping("/lua-test")
    public String testLua(@RequestParam String key,
                          @RequestParam String exp,
                          @RequestParam String next) {
        return luaScriptingService.checkAndSet(key, exp, next);
    }
}