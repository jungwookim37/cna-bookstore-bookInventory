package cnabookstore;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name="Deliverable_table")
public class Deliverable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long deliverableId;
    private Long orderId;
    private Long bookId;
    private Integer quantity;
    private String status;

    @PostPersist
    @PostUpdate
    public void onPostPersist() {
        if (getStatus() == "Delivery_Prepared") {
            DeliveryPrepared deliveryPrepared = new DeliveryPrepared();
            BeanUtils.copyProperties(this, deliveryPrepared);
            deliveryPrepared.publishAfterCommit();
        }
    }

    public Long getDeliverableId() {
        return deliverableId;
    }

    public void setDeliverableId(Long deliverableId) {
        this.deliverableId = deliverableId;
    }

    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getBookId(Long bookId) {
        return this.bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }


}
