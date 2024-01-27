package com.zkwd.server.game.gamestate;

import com.zkwd.server.game.exceptions.MoveException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Stores game information: the size of the board and placement of pieces on it.
 */
public class Board {

  public static final int BLACK = -1;
  public static final int WHITE = 1;
  public static final int FREE = 0;

  private int size;
  private int maxChainId;
  public Intersection[][] board;
  private ArrayList<Chain> chains;
  private String history = "";

  public Board(int size) {
    this.size = size;
    this.maxChainId = 0;
    this.board = new Intersection[size][size]; // by default every cell is 0
    this.chains = new ArrayList<Chain>();
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        board[i][j] = new Intersection(0);
      }
    }
    setNeighbours();
  }

  /**
   * Randomizes the board state. For debug purposes.
   * @return The board that has been randomized (this)
   */
  // Board randomize() {
  //   Random r = new Random();
  //   for (int i = 0; i < size; ++i) {
  //     for (int j = 0; j < size; ++j) {
  //       board[i][j].setState(r.nextInt() % 3 - 1);
  //     }
  //   }
  //   return this;
  // }

  public String getHistory() { return this.history; }

  public char toLetter(int i) {
    if (i >= 0 && i <= 25) {
      return (char)('a' + i);
    } else {
      return '?';
    }
  }
  public Board setBoard(String boardString) {

    int index = 0;

    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        char currentChar = boardString.charAt(index++);

        if (currentChar == 'W') {
          board[i][j].setState(1); // Assuming 1 represents the state for 'W'
          history = history + 'W' + toLetter(i) + toLetter(j);
        } else if (currentChar == 'B') {
          board[i][j].setState(-1); // Assuming -1 represents the state for 'B'
          history = history + 'B' + toLetter(i) + toLetter(j);
        } else {
          board[i][j].setState(0); // Assuming 0 represents the state for 'E'
        }
      }
      // Skip the '|' separator
      index++;
    }
    updateLiberties();
    setChains();
    updateLiberties();
    updateChainsLiberty();

    return this; // Assuming that you want to return the modified Board object
  }
  private void resetChains() {
    for (Chain ch : chains) {
      ch.reset();
    }
    chains.clear();
  }

  private void setChains() {
    resetChains();
    for (int x = 0; x < size; ++x) {
      for (int y = 0; y < size; ++y) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids = board[x][y].findChain();
        if (!(ids.isEmpty())) {
          for (int i = 0; i < ids.size(); ++i) {
            if (i != 0) {
              changeChain(ids.get(i), ids.get(0));
              deleteChain(ids.get(i));
            } else {
              board[x][y].setChainId(ids.get(0));
              for (Chain ch : chains) {
                if (ch.getId() == ids.get(0)) {
                  ch.addOne(board[x][y]);
                }
              }
            }
          }
        }

        ArrayList<Intersection> toGain = board[x][y].gainToChain();
        if (!(toGain.isEmpty())) {
          if (board[x][y].getChainId() == 0) {
            Chain newChain = createChain();
            newChain.addOne(board[x][y]);
            for (Intersection i : toGain) {
              newChain.addOne(i);
            }
          } else if (findChain(board[x][y].getChainId()) != null) {
            for (Intersection i : toGain) {
              findChain(board[x][y].getChainId()).addOne(i);
            }
          }
        }
      }
    }
  }

  /**
   * Gets the size of the board.
   * @return The size of the board
   */
  public int getSize() { return this.size; }

  // i think it isn't neccessary
  /**
   * Returns the state of the intersection (color).
   * @param x intersection's x coordinate
   * @param y intersection's y coordinate
   * @return state (0: free, 1: white, -1: black) of the intersection with
   *     coordinates x, y
   */
  public int getValue(int x, int y) { return board[x][y].getState(); }

  /**
   * Sets neighbours of each intersection and gives them numbers of liberties.
   */
  private void setNeighbours() {
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (i + 1 < size) {
          board[i][j].neighbours.add(board[i + 1][j]);
          board[i][j].addLiberty();
        }
        if (i - 1 >= 0) {
          board[i][j].neighbours.add(board[i - 1][j]);
          board[i][j].addLiberty();
        }
        if (j + 1 < size) {
          board[i][j].neighbours.add(board[i][j + 1]);
          board[i][j].addLiberty();
        }
        if (j - 1 >= 0) {
          board[i][j].neighbours.add(board[i][j - 1]);
          board[i][j].addLiberty();
        }
      }
    }
  }

  /**
   * Returns the board in a String format. The encoding is specified as
   *following: <pre> white -> W black -> B free  -> E
   * </pre>
   * The board is scanned column by column, and columns are divided by a "|"
   *symbol
   * @return a String object that describes the current board state
   */
  public String prepareBoardString() {
    String out = "";
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        int k = board[i][j].getState();
        out += (k == 1) ? "W" : (k == -1) ? "B" : "E";
      }
      out += "|";
    }
    return out;
  }

  /**
   * Commits the move specified by location and player color, if said move is
   * valid.
   * @param x coordinate
   * @param y coordinate
   * @param playerColor -1 for black, 1 for white
   * @throws MoveException if the specified move is invalid.
   */
  public void putStone(int x, int y, int playerColor) throws MoveException {
    if (correctMove(x, y, playerColor)) {
      board[x][y].setState(playerColor);

      char color = 'W';
      if (playerColor == -1) {
        color = 'B';
      }
      history = history + color + toLetter(x) + toLetter(y);
      board[x][y].takeLiberties();

      // new stone joins existing chain
      ArrayList<Integer> ids = new ArrayList<Integer>();
      ids = board[x][y].findChain();
      if (!(ids.isEmpty())) {
        for (int i = 0; i < ids.size(); ++i) {
          if (i != 0) {
            changeChain(ids.get(i), ids.get(0));
            deleteChain(ids.get(i));
          } else {
            board[x][y].setChainId(ids.get(0));
            for (Chain ch : chains) {
              if (ch.getId() == ids.get(0)) {
                ch.addOne(board[x][y]);
              }
            }
          }
        }
      }
      // gain all lonley stones around newly put stone and try to add them to
      // chain
      ArrayList<Intersection> toGain = board[x][y].gainToChain();
      if (!(toGain.isEmpty())) {
        if (board[x][y].getChainId() == 0) {
          Chain newChain = createChain();
          newChain.addOne(board[x][y]);
          for (Intersection i : toGain) {
            newChain.addOne(i);
          }
        } else if (findChain(board[x][y].getChainId()) != null) {
          for (Intersection i : toGain) {
            findChain(board[x][y].getChainId()).addOne(i);
          }
        }
      }
      updateLiberties();
      updateChainsLiberty();
    } else {
      throw new MoveException();
    }
  }

  private void updateLiberties() {
    for (int i = 0; i != size; ++i) {
      for (int j = 0; j != size; ++j) {
        board[i][j].updateLiberty();
      }
    }
  }

  /**
   * Removes a stone from the board, if one exists.
   * @param x coordinate
   * @param y coordinate
   */
  private void removeStone(int x, int y) {
    board[x][y].setState(FREE);
    board[x][y].returnLiberties();
  }

  /**
   * Checks for move validity.
   * @param x - first coordinate of the intersection
   * @param y - second coordinate of the intersection
   * @param payerColor -1 for BLACK, 1 for WHITE
   * @return true if the intersetion is FREE and suicide isn't commited or is
   *     but is also a capturing move and Ko rule isn't broken.
   */
  public boolean correctMove(int x, int y, int playerColor) {
    boolean inbounds = (x < size && x >= 0 && y < size && y >= 0);
    boolean free = (board[x][y].getState() == FREE);
    boolean suicide = true;
    boolean capturing = false;
    // boolean ko = Ko(x, y, playerColor);
    boolean ko = false;
    for (Intersection i : board[x][y].neighbours) {
      if (i.getState() == FREE) {
        suicide = false;
      } else if (i.getState() == playerColor) {
        if (i.getChainId() != 0) {
          for (Chain ch : chains) {
            if (ch.getId() == i.getChainId() && ch.getLiberty() > 1) {
              suicide = false;
            }
          }
        } else if (i.getLiberty() > 1) {
          suicide = false;
        }
      } else if (i.getLiberty() == 1) {
        if (i.getChainId() == 0) {
          board[x][y].capturing = true;
          capturing = true;
        } else {
          for (Chain ch : chains) {
            if (ch.getId() == i.getChainId() && ch.getLiberty() == 1) {
              board[x][y].capturing = true;
              capturing = true;
            }
          }
        }
      }
    }
    return inbounds && free && (!suicide || capturing) && !ko;
  }

  private Chain findChain(int chainId) {
    for (Chain ch : chains) {
      if (ch.getId() == chainId) {
        return ch;
      }
    }
    return null;
  }

  private Chain createChain() {
    Chain newChain = new Chain(maxChainId + 1);
    ++maxChainId;
    chains.add(newChain);
    return newChain;
  }

  private void changeChain(int currentId, int newId) {
    ArrayList<Intersection> stones = new ArrayList<>();
    Iterator<Chain> iterator = chains.iterator();

    while (iterator.hasNext()) {
      Chain ch = iterator.next();
      if (ch.getId() == currentId) {
        ch.changeId(newId);
        stones = ch.chain;
        iterator.remove();
      }
    }
    // Move stones to new chain
    for (Chain ch : chains) {
      if (ch.getId() == newId) {
        for (Intersection i : stones) {
          ch.addOne(i);
        }
      }
    }
  }

  private void deleteChain(int id) {
    if (!(chains.isEmpty())) {
      for (Chain ch : chains) {
        if (ch.getId() == id) {
          chains.remove(ch);
        }
      }
    }
  }

  private void removeChain(Chain chain) {
    chain.removeStones();
    chains.remove(chain);
  }

  public void removeCapturedStones() {
    for (int i = 0; i != size; ++i) {
      for (int j = 0; j != size; ++j) {
        if (board[i][j].getState() != FREE && board[i][j].getLiberty() <= 0 &&
            board[i][j].getChainId() == 0 && !board[i][j].capturing) {
          removeStone(i, j);
        }
        if (board[i][j].capturing) {
          board[i][j].capturing = false;
        }
      }
    }
  }
  public void removeCapturedChains() {
    ArrayList<Chain> toRemove = new ArrayList<Chain>();
    for (Chain ch : chains) {
      ch.updateLiberty();
      if (ch.getLiberty() <= 0) {
        toRemove.add(ch);
      }
    }
    for (Chain ch : toRemove) {
      removeChain(ch);
    }
  }

  private void updateChainsLiberty() {
    for (Chain ch : chains) {
      ch.updateLiberty();
    }
  }

  public boolean Ko(int x, int y, int playerColor) {
    int neighboursCounter = 0;
    boolean captureKo = false;
    for (Intersection i : board[x][y].neighbours) {
      if (i.getState() == -playerColor) {
        ++neighboursCounter;
        if (i.getLiberty() == 1 && i.getChainId() == 0) {
          captureKo = true;
        }
      }
    }
    return captureKo && neighboursCounter == 4;
  }

  public int getChainLiberty(int id) {
    for (Chain ch : chains) {
      if (ch.getId() == id) {
        return ch.getLiberty();
      }
    }
    return -1;
  }
}
