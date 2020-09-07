package cnabookstore;

import cnabookstore.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;


@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    DeliverableRepository deliverableRepository;
    @Autowired
    BookRepository bookRepository;

    @StreamListener(KafkaProcessor.INPUT)
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void wheneverOrdered_PrepareDelivery(@Payload Ordered ordered){

        if(ordered.isMe()){

            /*Deliberables 생성 가능 여부 확인. 주문양 >=재고 */
            Optional<Book> bookOptional = bookRepository.findById(ordered.getBookId());
            Book book = bookOptional.get();

            System.out.println("##### Deliberables Cheeck :" +book.getStock() + "vs" +ordered.getQuantity());
            if (book.getStock() >= ordered.getQuantity()) {

                /*Deliberables 생성*/
                System.out.println("##### Delivery_Prepared : " + ordered.toJson());
                Deliverable deliverable = new Deliverable();
                deliverable.setOrderId(ordered.getOrderId());
                deliverable.setQuantity(ordered.getQuantity());
                deliverable.setBookId(ordered.getBookId());
                deliverable.setStatus("Delivery_Prepared");

                deliverableRepository.save(deliverable);

                /*Book 재고 차감*/
                book.setStock( book.getStock() - ordered.getQuantity() );
                bookRepository.save(book);

            }
            else{
                System.out.println("##### Stock_Lacked : " + ordered.toJson());
                Deliverable deliverable = new Deliverable();
                deliverable.setOrderId(ordered.getOrderId());
                deliverable.setQuantity(ordered.getQuantity());
                deliverable.setBookId(ordered.getBookId());
                deliverable.setStatus("Stock_Lacked");

                deliverableRepository.save(deliverable);
            }

        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_DeleteDeliverable(@Payload OrderCanceled orderCanceled){

        if(orderCanceled.isMe()){
            System.out.println("##### listener DeleteDeliverable : " + orderCanceled.toJson());
            Optional<Deliverable> deliverableOptional = deliverableRepository.findByOrderId(orderCanceled.getOrderId());
            Deliverable deliverable = deliverableOptional.get();
            deliverable.setStatus("DeliveryCanceled");

            deliverableRepository.save(deliverable);
        }
    }

}