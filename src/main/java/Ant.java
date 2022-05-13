import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ant {
    private List<Integer> history;
    private Integer length;

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


