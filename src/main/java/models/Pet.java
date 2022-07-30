package models;

import lombok.Data;
import java.util.Date;

@Data //having implicit @Getter, @Setter, @ToString, @EqualsAndHashCode and @RequiredArgsConstructor
public class Pet { //create class and assign of data

    private int id;
    private int petId ;
    private int quantity;//1 or 0
    private Date shipDate ;
    private String status ;
    private String complete ;


}