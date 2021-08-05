package top.pin90.server.dao.treehole;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import top.pin90.common.po.treehole.TreeHole;

public interface TreeHoleRepository extends ReactiveMongoRepository<TreeHole, ObjectId> {

}
