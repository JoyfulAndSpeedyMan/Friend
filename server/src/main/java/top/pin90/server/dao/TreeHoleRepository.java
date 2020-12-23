package top.pin90.server.dao;

import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import top.pin90.server.po.TreeHole;

public interface TreeHoleRepository extends ReactiveSortingRepository<TreeHole, ObjectId> {

}
