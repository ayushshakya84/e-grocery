package ru.voleshko.grocery.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class FrontendController {

    @GetMapping("/product-catalogue")
    public String returnProductCataloguePage() {
        return "product";
    }

    @GetMapping("/cart")
    public String returnCartPage() {
        return "cart";
    }

    @GetMapping("/payment")
    public String returnPaymentPage() {
        return "payment";
    }

    @GetMapping("/order")
    public String returnOrderPage() {
        return "order";
    }

    @GetMapping("/order-accepted")
    public String returnOrderAcceptedPage() {
        return "order_accepted";
    }

    @GetMapping("/user-profile")
    public String returnUserProfilePage() {
        return "profile";
    }

    @GetMapping("/product-edit")
    public String returnUserProductEditPage() {
        return "product-edit";
    }

    @GetMapping("/product-management")
    public String returnUserProductManagementPage() {
        return "product-management";
    }

    @GetMapping(path = "/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "product";
    }
}
