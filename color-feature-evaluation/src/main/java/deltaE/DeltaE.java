package deltaE;

import org.vitrivr.cineast.core.color.LabContainer;

public class DeltaE {

  LabContainer l1;
  LabContainer l2;
  double dE;

  DeltaE(LabContainer l1, LabContainer l2) {
    this.l1 = l1;
    this.l2 = l2;
    dE = calculateDeltaE(l1, l2);
  }

  public double calculateDeltaE(LabContainer l1, LabContainer l2) {

    float dL = l1.getL() - l2.getL();
    float dA = l1.getA() - l2.getA();
    float dB = l1.getB() - l2.getB();

    // calculate deltaE
    return Math.sqrt(Math.pow(dL, 2) + Math.pow(dA, 2) + Math.pow(dB, 2));
  }

  public void print() {
    System.out.println("DeltaE of c1: " + l1.toString() + "and c2: " + l2.toString() + " = " + dE);
  }

}
