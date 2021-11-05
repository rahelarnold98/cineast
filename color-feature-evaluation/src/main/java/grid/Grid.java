package grid;

import java.util.ArrayList;
import java.util.List;

public class Grid {

  public List<Field> grid = new ArrayList<>();

  public Grid() {
  }

  public void append(Field f) {
    grid.add(f);
  }

  public List<Field> getGrid() {
    return grid;
  }

  public void setGrid(List<Field> grid) {
    this.grid = grid;
  }
}

