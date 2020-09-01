package cnabookstore;

public class InventoyIncreased  extends AbstractEvent{
    private Long bookId;
    private Integer stock;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
