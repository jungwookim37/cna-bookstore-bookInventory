package cnabookstore;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockInputController {
    Logger logger = LoggerFactory.getLogger(StockInputController.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private DeliverableRepository deliverableRepository;

    @Autowired
    private StockInputRepository stockInputRepository;

    @GetMapping({"/stockInputs"})
    public Iterable<StockInput> getStockInputs() {
        return this.stockInputRepository.findAll();
    }

    @GetMapping({"/stockInputs/{id}"})
    public StockInput findOne(@PathVariable Long id) {
        return this.stockInputRepository.findById(id).get();
    }

    @PostMapping({"/stockInputs"})
    public StockInput createNewStocks(@RequestBody StockInput stockInput) throws Exception {

        Book book;
        logger.info("################## StockInput Post is called....");

        if (stockInput == null)
            throw new Exception("No input data ");

        System.out.println("################ bookRepository.findByBookId Running for book id : ..." + stockInput.getBookId());
        BookRepository bookRepository = BookInventoryApplication.applicationContext.getBean(BookRepository.class);
        Optional<Book> bookOptional = bookRepository.findById(stockInput.getBookId());

        if (bookOptional.isPresent()) {
            book = bookOptional.get();
        } else {
            throw new Exception("No book for book id " + stockInput.getBookId());
        }

        Integer totalQuantity = Integer.valueOf(book.getStock().intValue() + stockInput.getQuantity().intValue());
        System.out.println("################ total quantity " + totalQuantity.toString());
        DeliverableRepository deliverableRepository = BookInventoryApplication.applicationContext.getBean(DeliverableRepository.class);
        Optional<List<Deliverable>> deliverablesOptional = deliverableRepository.findByBookIdAndStatusOrderByOrderIdAsc(book.getBookId(), "Stock_Lacked");
        if (deliverablesOptional.isPresent()) {
            List<Deliverable> deliverables = deliverablesOptional.get();
            for (Deliverable deliverable : deliverables) {
                if (totalQuantity.intValue() >= deliverable.getQuantity().intValue()) {
                    deliverable.setStatus("Delivery_Prepared");
                    deliverableRepository.save(deliverable);
                    totalQuantity = Integer.valueOf(totalQuantity.intValue() - deliverable.getQuantity().intValue());
                    System.out.println("################ saved, deliverable id " + deliverable.getDeliverableId());
                }
            }
        }

        book.setStock(totalQuantity);
        System.out.println("################ saved, book id " + book.getBookId() + ", stock " + book.getStock());
        bookRepository.save(book);
        return stockInputRepository.save(stockInput);
    }
}
