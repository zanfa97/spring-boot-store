package com.codewithmosh.store.payments;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.orders.Order;
import com.codewithmosh.store.orders.OrderItem;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;

@Service
public class StripePaymentGateway implements PaymentGateway {

    private static final Logger LOG = LoggerFactory.getLogger(StripePaymentGateway.class);

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try {
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success=orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel.html")
                    .putMetadata("orderId", order.getId().toString());

            order.getItems().forEach(item -> {
                var lineItem = createLineItem(item);
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());
            return new CheckoutSession(session.getUrl());

        } catch (StripeException ex) {
            LOG.error(ex.getMessage());
            throw new PaymentException();
        }
    }

    private LineItem createLineItem(OrderItem item) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(createPriceData(item))
                .build();
    }

    private PriceData createPriceData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("USD")
                .setUnitAmountDecimal(item.getUnitPrice().multiply(BigDecimal.valueOf(100)))
                .setProductData(
                        createProductData(item))
                .build();
    }

    private ProductData createProductData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName())
                .build();
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            var payload = request.getPayload();
            var signature = request.getHeaders().get("stripe-signature");
            var event = Webhook.constructEvent(payload, signature, webhookSecretKey);

            return switch (event.getType()) {
                case "payment_intent.succeeded" ->
                    Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.PAID));
                case "payment_intent.payment_failed" ->
                    Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.FAILED));
                default ->
                    Optional.empty();
            };

        } catch (SignatureVerificationException ex) {
            throw new PaymentException("Invalide signature");
        }

    }

    private Long extractOrderId(Event event) {
        var stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
            () -> new PaymentException("Could not deserialize Strip event. Check the SDK and API version.")
        );
        var paymentIntent = (PaymentIntent) stripeObject;
        return Long.valueOf(paymentIntent.getMetadata().get("order_id"));
    }

}