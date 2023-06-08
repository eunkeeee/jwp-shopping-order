package cart.application;

import cart.domain.Member;
import cart.domain.Order;
import cart.domain.Product;
import cart.dto.request.CartItemIdRequest;
import cart.dto.request.PayRequest;
import cart.dto.response.PayResponse;
import cart.exception.InvalidPriceException;
import cart.repository.CartItemRepository;
import cart.repository.MemberRepository;
import cart.repository.OrderRepository;
import cart.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class PayService {

    private static final double ACCUMULATION_RATE = 0.05;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    public PayService(
            final MemberRepository memberRepository,
            final ProductRepository productRepository,
            final CartItemRepository cartItemRepository,
            final OrderRepository orderRepository
    ) {
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public PayResponse orderCartItems(final Member member, final PayRequest request) {
        final int savedPoint = memberRepository.findPointOf(member);
        final int usedPoint = request.getPoints();
        final Map<Product, Integer> products = findProductQuantity(request, member.getId());

        final Order order = new Order(usedPoint, products, member);

        final int originalPrice = order.calculateOriginalPrice();
        validateSamePrice(originalPrice, request.getOriginalPrice());

        final int orderPrice = order.calculateOrderPrice();

        final int updatedPoint = order.calculatedUpdatedPoint(savedPoint);

        final long orderHistoryId = orderRepository.createOrderHistory(member, originalPrice, usedPoint, orderPrice);

        products.keySet()
                .forEach(product -> {
                            final Integer quantity = products.get(product);
                            orderRepository.addOrderProductTo(
                                    orderHistoryId,
                                    product,
                                    quantity
                            );
                        }
                );

        final List<Long> cartItemIds = request.getCartItemIds()
                .stream().map(CartItemIdRequest::getCartItemId)
                .collect(Collectors.toList());

        for (final Long cartItemId : cartItemIds) {
            cartItemRepository.deleteById(cartItemId);
        }

        memberRepository.updatePoint(member, updatedPoint + (int) (orderPrice * ACCUMULATION_RATE));
        return new PayResponse(orderHistoryId);
    }

    private Map<Product, Integer> findProductQuantity(final PayRequest request, final Long memberId) {
        return request.getCartItemIds().stream()
                .map(CartItemIdRequest::getCartItemId)
                .filter(isCartItemOf(memberId))
                .collect(Collectors.toMap(
                        id -> {
                            final long productId = cartItemRepository.findProductIdOf(id);
                            return productRepository.getProductById(productId);
                        },
                        cartItemRepository::findQuantityOf
                ));
    }

    private Predicate<Long> isCartItemOf(final Long memberId) {
        return cartItemId -> cartItemRepository.findById(cartItemId).getMember().getId().equals(memberId);
    }

    private static void validateSamePrice(final int originalPrice, final int displayPrice) {
        if (displayPrice != originalPrice) {
            throw new InvalidPriceException("현재 구매 가격과 일치하지 않습니다!");
        }
    }
}
