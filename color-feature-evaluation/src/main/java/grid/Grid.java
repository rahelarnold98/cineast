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

class Field {

  int index;
  String color;

  Field(int index, String color) {
    this.index = index;
    this.color = color;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }
}
