package agmg.orderms.config;

import agmg.orderms.model.DatabaseOrderSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class SequenceInitializer implements CommandLineRunner {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {
        if(mongoOperations.findById("order_sequence", DatabaseOrderSequence.class) == null) {
            DatabaseOrderSequence initialSequence = new DatabaseOrderSequence();
            initialSequence.setId("order_sequence");
            initialSequence.setSeq(0);
            mongoOperations.insert(initialSequence, "sequence");
        }
    }
}
