package cart.ui;

import cart.application.PayService;
import cart.domain.Member;
import cart.dto.request.PayRequest;
import cart.dto.response.PayResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PayApiController {

    private final PayService payService;

    public PayApiController(final PayService payService) {
        this.payService = payService;
    }

    @PostMapping("/pay")
    public ResponseEntity<PayResponse> orderCartItems(
            Member member,
            @Valid @RequestBody PayRequest request
    ) {
        final PayResponse response = payService.orderCartItems(member, request);
        return ResponseEntity
                .ok()
                .body(response);
    }
}
