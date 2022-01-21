package iooojik.dev.anon.api.rest.http

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/*")
class MainController {
    @GetMapping("/")
    fun index() : String = "index"

    @GetMapping("/policy")
    fun policy() : String = "policy"

    @GetMapping("/terms")
    fun terms() : String = "terms"

    @GetMapping("/terms_and_policy")
    fun termsAndPolicy() : String = "terms_and_policy"
}