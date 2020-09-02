package cnabookstore;

import cnabookstore.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    DeliverableRepository deliverableRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_PrepareDelivery(@Payload Ordered ordered){

        if(ordered.isMe()){
            System.out.println("##### listener PrepareDelivery : " + ordered.toJson());
            Deliverable deliverable = new Deliverable();
            deliverable.setOrderId(ordered.getOrderId());
            deliverable.setQuantity(ordered.getQuantity());
            deliverable.setBookId(ordered.getBookId());
            deliverable.setStatus("DeliveryPrepared");

            deliverableRepository.save(deliverable);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_DeleteDeliverable(@Payload OrderCanceled orderCanceled){

        if(orderCanceled.isMe()){
            System.out.println("##### listener DeleteDeliverable : " + orderCanceled.toJson());
            Optional<Deliverable> deliverableOptional = deliverableRepository.findById(orderCanceled.getOrderId());
            Deliverable deliverable = deliverableOptional.get();
            deliverable.setStatus("DeliveryCanceled");

            deliverableRepository.save(deliverable);
        }
    }

}