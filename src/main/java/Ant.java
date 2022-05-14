import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;


@NoArgsConstructor
@Data
public class Ant {
    private List<Integer> history;
    private Integer length;
    private Integer size;

    public void addToHistory(Integer vertex){
        this.history.add(vertex);
    }

    public void updateLength(Integer value){
        this.length += value;
    }
}

class SortByLength implements Comparator<Ant>{
    public int compare(Ant a, Ant b)
    {
        return b.getHistory().size() - a.getHistory().size();
    }
}


