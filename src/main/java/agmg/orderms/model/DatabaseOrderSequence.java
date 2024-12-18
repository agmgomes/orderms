package agmg.orderms.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "sequence")
public class DatabaseOrderSequence {
    @Id
    private String id;

    private long seq;
}
