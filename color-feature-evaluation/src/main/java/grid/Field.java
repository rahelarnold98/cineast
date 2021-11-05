package grid;

class Field {

  int index;
  String color;

  Field() {
  }

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
