package org.mercycorps.translationcards.txcmaker.api.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class SpringResource {

    @RequestMapping(method= RequestMethod.GET)
    public String index() {
        return "hi";
    }
}
