import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;

//utils class
class Utils {

  // counts the mines surrounding and including a cell at given coordinates
  public int countMines(ArrayList<ArrayList<ACell>> game, int row, int col) {
    return this.minesHelper(game, row, col - 1) + this.minesHelper(game, row - 1, col - 1)
        + this.minesHelper(game, row + 1, col - 1);
  }

  // countMines helper
  public int minesHelper(ArrayList<ArrayList<ACell>> game, int row, int col) {
    int count = 0;
    if (row > game.size() - 1 || row < 0) {
      return count;
    }
    else {
      for (int p = 0; p < 3; p++) {
        if (col + p <= game.get(0).size() - 1 && col + p >= 0) {
          count = count + game.get(row).get(col + p).isMine();
        }
      }
    }
    return count;
  }

  // makes the cells
  ArrayList<ArrayList<ACell>> makeCells(Random rand, int row, int col, int mines) {
    ArrayList<ArrayList<ACell>> current = new ArrayList<>(row);

    for (int i = 1; i <= row; i++) {
      ArrayList<ACell> rows = new ArrayList<>();
      for (int p = 1; p <= col; p++) {
        rows.add(new Cell());
      }
      current.add(rows);
    }

    for (int k = mines; k > 0; k--) {
      int randRow = rand.nextInt(row);
      int randCol = rand.nextInt(col);

      if (current.get(randRow).get(randCol).isMine() == 1) {
        k++;
      }
      current.get(randRow).set(randCol, new Mine());
    }
    return current;
  }
}

//minesweeper class
class Minesweeper extends World {
  ArrayList<ArrayList<ACell>> game;
  WorldScene world;
  Random rand;
  int numEmpties;

  Minesweeper(int row, int col, int mines) {

    rand = new Random();
    game = new Utils().makeCells(rand, row, col, mines);
    world = new WorldScene(col * 20, row * 20);
    numEmpties = (row * col) - mines;
  }

  Minesweeper(int row, int col, int mines, Random rand) {

    game = new Utils().makeCells(rand, row, col, mines);
    world = new WorldScene(col * 20, row * 20);
    this.rand = rand;
    numEmpties = (row * col) - mines;
  }

  // makes the scene
  public javalib.impworld.WorldScene makeScene() {
    for (int i = 0; i < this.game.size(); i++) {

      for (int p = 0; p < this.game.get(i).size(); p++) {
        this.game.get(i).get(p).drawCell(this, i, p);

      }
    }
    return this.world;

  }

  //creates world on each tick
  public void onTick() {
    for (int i = 0; i < this.game.size(); i++) {
      for (int p = 0; p < this.game.get(i).size(); p++) {
        ACell cell = this.game.get(i).get(p);
        if (new Utils().countMines(this.game, i, p) == 0 && cell.visible) {
          if (cell.visible && i < this.game.size() - 1
              && this.game.get(i + 1).get(p).isMine() == 0) {
            this.game.get(i + 1).get(p).visible = true;
          }
          if (cell.visible && i > 0 && this.game.get(i - 1).get(p).isMine() == 0) {
            this.game.get(i - 1).get(p).visible = true;
          }
          if (cell.visible && i < this.game.size() - 1 && p < this.game.get(0).size() - 1
              && this.game.get(i + 1).get(p + 1).isMine() == 0) {
            this.game.get(i + 1).get(p + 1).visible = true;
          }
          if (cell.visible && i < this.game.size() - 1 && p > 0
              && this.game.get(i + 1).get(p - 1).isMine() == 0) {
            this.game.get(i + 1).get(p - 1).visible = true;
          }
          if (cell.visible && i > 0 && p < this.game.get(0).size() - 1
              && this.game.get(i - 1).get(p + 1).isMine() == 0) {
            this.game.get(i - 1).get(p + 1).visible = true;
          }
          if (cell.visible && i > 0 && p > 0 && this.game.get(i - 1).get(p - 1).isMine() == 0) {
            this.game.get(i - 1).get(p - 1).visible = true;
          }
          if (cell.visible && p < this.game.get(0).size() - 1
              && this.game.get(i).get(p + 1).isMine() == 0) {
            this.game.get(i).get(p + 1).visible = true;
          }
          if (cell.visible && p > 0 && this.game.get(i).get(p - 1).isMine() == 0) {
            this.game.get(i).get(p - 1).visible = true;
          }

        }
      }
    }

  }

  // on mouse click
  public void onMouseClicked(Posn posn, String button) {
    int colNum = posn.x / 20;
    int rowNum = posn.y / 20;

    if (colNum < this.game.get(0).size() && rowNum < this.game.size()) {

      if (button.equals("LeftButton")) {
        this.game.get(rowNum).get(colNum).visible = true;
      }

      if (button.equals("RightButton") && !this.game.get(rowNum).get(colNum).visible) {
        this.game.get(rowNum).get(colNum).flagged = !this.game.get(rowNum).get(colNum).flagged;
      }
    }
  }

  // end of game
  public WorldEnd worldEnds() {
    int count = 0;
    for (int i = 0; i < this.game.size(); i++) {

      for (int p = 0; p < this.game.get(i).size(); p++) {
        ACell cell = this.game.get(i).get(p);
        if (cell.isMine() == 1 && cell.visible) {
          return new WorldEnd(true, this.makeAFinalScene("You Lose"));
        }
        if (cell.isMine() == 0 && cell.visible) {
          count++;
        }
        if (count == this.numEmpties) {
          return new WorldEnd(true, this.makeAFinalScene("You Win!!!"));
        }
      }
    }
    return new WorldEnd(false, this.makeScene());
  }

  // final scene
  public WorldScene makeAFinalScene(String message) {
    for (int i = 0; i < this.game.size(); i++) {

      for (int p = 0; p < this.game.get(i).size(); p++) {
        ACell cell = this.game.get(i).get(p);
        cell.visible = true;
        cell.drawCell(this, i, p);
      }
    }
    this.world.placeImageXY(new TextImage(message, 50, Color.BLACK),
        this.game.get(0).size() * 20 / 2, this.game.size() * 20 / 2);
    return this.world;
  }
}

// abstracts cell type
abstract class ACell {

  boolean visible;
  boolean flagged;

  ACell(boolean visible) {
    this.visible = visible;
    this.flagged = false;
  }

  // returns 1 if it is a mine
  abstract int isMine();

  // draws cell
  abstract void drawCell(Minesweeper sweep, int rowNum, int colNum);
}

//mine
class Mine extends ACell {

  Mine() {
    super(false);
  }

  Mine(boolean visible) {
    super(visible);
  }

  // returns 1 if it is a mine
  int isMine() {
    return 1;
  }

  // draws cell
  public void drawCell(Minesweeper sweep, int rowNum, int colNum) {
    if (!visible) {
      sweep.world.placeImageXY(
          new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.darkGray),
              new RectangleImage(20, 20, OutlineMode.SOLID, Color.GRAY)),
          colNum * 20 + 10, rowNum * 20 + 10);
      if (this.flagged) {
        sweep.world.placeImageXY(new EquilateralTriangleImage(10, OutlineMode.SOLID, Color.YELLOW),
            colNum * 20 + 10, rowNum * 20 + 10);
      }
    }
    else {
      sweep.world.placeImageXY(new StarImage(8, OutlineMode.SOLID, Color.RED), colNum * 20 + 10,
          rowNum * 20 + 10);
    }
  }

}

//represents a cell 
class Cell extends ACell {

  Cell() {
    super(false);
  }

  Cell(boolean visible) {
    super(visible);
  }

  // returns 1 if it a mine
  int isMine() {
    return 0;
  }

  // draws cell
  public void drawCell(Minesweeper sweep, int rowNum, int colNum) {
    int numMines = new Utils().countMines(sweep.game, rowNum, colNum);

    if (!this.visible) {
      sweep.world.placeImageXY(
          new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.darkGray),
              new RectangleImage(20, 20, OutlineMode.SOLID, Color.GRAY)),
          colNum * 20 + 10, rowNum * 20 + 10);
      if (this.flagged) {
        sweep.world.placeImageXY(new EquilateralTriangleImage(10, OutlineMode.SOLID, Color.YELLOW),
            colNum * 20 + 10, rowNum * 20 + 10);
      }
    }

    if (this.visible) {
      sweep.world.placeImageXY(
          new OverlayImage(new RectangleImage(20, 20, OutlineMode.SOLID, Color.lightGray),
              new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.GRAY)),
          colNum * 20 + 10, rowNum * 20 + 10);
      if (numMines > 0) {
        sweep.world.placeImageXY(new TextImage(Integer.toString(numMines), 8, Color.BLUE),
            colNum * 20 + 10, rowNum * 20 + 10);
      }
    }

  }
}

//examples
class ExamplesGame {

  Minesweeper minesweeper;
  Minesweeper minesweeper1;
  Minesweeper minesweeper2;
  Minesweeper minesweeper3;
  Minesweeper minesweeper4;
  ACell mine;
  ACell mineVisible;
  ACell cell;

  // reset
  void reset() {
    minesweeper = new Minesweeper(3, 4, 3, new Random(1));
    minesweeper1 = new Minesweeper(5, 5, 20, new Random(2));
    minesweeper2 = new Minesweeper(1, 1, 0, new Random(3));
    minesweeper3 = new Minesweeper(1, 1, 1, new Random(4));
    minesweeper4 = new Minesweeper(1, 1, 1, new Random(4));
    mine = new Mine();
    cell = new Cell();
    mineVisible = new Mine(true);
  }

  // tests countMines
  boolean testCountMines(Tester t) {
    reset();

    return t.checkExpect(new Utils().countMines(this.minesweeper.game, 2, 2), 1)
        && t.checkExpect(new Utils().countMines(this.minesweeper1.game, 4, 4), 4)
        && t.checkExpect(new Utils().countMines(this.minesweeper1.game, 4, 2), 6)
        && t.checkExpect(new Utils().countMines(this.minesweeper1.game, 0, 0), 2);
  }

  // tests countMinesHelper
  boolean testCountMinesHelper(Tester t) {
    reset();

    return t.checkExpect(new Utils().minesHelper(this.minesweeper.game, 2, 1), 0)
        && t.checkExpect(new Utils().minesHelper(this.minesweeper1.game, 4, 3), 2)
        && t.checkExpect(new Utils().minesHelper(this.minesweeper1.game, 4, 1), 3)
        && t.checkExpect(new Utils().minesHelper(this.minesweeper1.game, 0, -1), 1);
  }

  // tests makeCell
  boolean testMakeCells(Tester t) {
    reset();

    return t.checkExpect(new Utils().makeCells(new Random(1), 3, 4, 3), this.minesweeper.game)
        && t.checkExpect(new Utils().makeCells(new Random(2), 5, 5, 20), this.minesweeper1.game);
  }

  // tests makeScene
  boolean testMakeScene(Tester t) {
    reset();
    this.minesweeper2.makeScene();
    WorldScene world1 = new WorldScene(20, 20);
    world1.placeImageXY(
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.darkGray),
            new RectangleImage(20, 20, OutlineMode.SOLID, Color.GRAY)),
        10, 10);

    return t.checkExpect(this.minesweeper2.world, world1);
  }

  // tests isMine
  boolean testIsMine(Tester t) {
    reset();
    return t.checkExpect(this.mine.isMine(), 1) && t.checkExpect(this.cell.isMine(), 0);
  }

  // tests drawCell
  boolean testDrawCell(Tester t) {
    reset();
    WorldScene world1 = new WorldScene(20, 20);
    WorldScene world2 = new WorldScene(20, 20);
    WorldScene world3 = new WorldScene(20, 20);
    this.cell.drawCell(minesweeper2, 0, 0);
    this.mine.drawCell(minesweeper3, 0, 0);
    this.mineVisible.drawCell(minesweeper4, 0, 0);

    world1.placeImageXY(
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.darkGray),
            new RectangleImage(20, 20, OutlineMode.SOLID, Color.GRAY)),
        10, 10);

    world2.placeImageXY(
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.darkGray),
            new RectangleImage(20, 20, OutlineMode.SOLID, Color.GRAY)),
        10, 10);

    world3.placeImageXY(new StarImage(8, OutlineMode.SOLID, Color.RED), 10, 10);

    return t.checkExpect(this.minesweeper2.world, world1)
        && t.checkExpect(this.minesweeper3.world, world2)
        && t.checkExpect(this.minesweeper4.world, world3);
  }

  // test MakeFinalScene
  boolean testMakeAFinalScene(Tester t) {
    reset();
    this.minesweeper4.makeAFinalScene("");
    return t.checkExpect(this.minesweeper4.makeAFinalScene(""), this.minesweeper4.makeScene());
  }

  // test World end
  boolean testWorldEnd(Tester t) {
    reset();
    this.minesweeper4.game.get(0).get(0).visible = true;
    return t.checkExpect(this.minesweeper4.worldEnds(),
        new WorldEnd(true, this.minesweeper4.makeAFinalScene("You Win!!!")));
  }

  // test World end
  boolean testOnMouseClicked(Tester t) {
    reset();
    this.minesweeper4.onMouseClicked(new Posn(10, 10), "LeftButton");
    return t.checkExpect(this.minesweeper4.game.get(0).get(0).visible, true);
  }

  // test onTick
  boolean testOnTick(Tester t) {
    reset();
    this.minesweeper4.onTick();
    return t.checkExpect(this.minesweeper4, this.minesweeper3);
  }

  // runs program and used to test methods visually as well
  void testbigBang(Tester t) {
    Minesweeper world = new Minesweeper(20, 20, 60);
    int worldWidth = 5000;
    int worldHeight = 5000;
    double tickRate = 0.05;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }
}