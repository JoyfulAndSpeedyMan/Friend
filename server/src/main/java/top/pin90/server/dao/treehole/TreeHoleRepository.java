package top.pin90.server.dao.treehole;

import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import top.pin90.common.po.treehole.TreeHole;

public interface TreeHoleRepository extends ReactiveSortingRepository<TreeHole, ObjectId> {

}
