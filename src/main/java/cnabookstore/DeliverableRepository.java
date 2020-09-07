package cnabookstore;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface DeliverableRepository extends PagingAndSortingRepository<Deliverable, Long>{

    Optional<List<Deliverable>> findByBookIdAndStatusOrderByOrderIdAsc(Long bookId, String stockLacked);
}