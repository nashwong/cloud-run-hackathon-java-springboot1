package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

@SpringBootApplication
@RestController
public class Application {

  private String href = null;
  private Stack<String> cmdSeq = new Stack<String>();
  private int healthNum = 0;
  private int curScore = 0;
  private boolean pointIncrease;
  private PlayerState playerState;
  private String direction;
  private Map<String, PlayerState> arenaState;

  static class Self {
    public String href;
  }

  static class Links {
    public Self self;
  }

  static class PlayerState {
    public Integer x;
    public Integer y;
    public String direction;
    public Boolean wasHit;
    public Integer score;
  }

  static class Arena {
    public List<Integer> dims;
    public Map<String, PlayerState> state;
  }

  static class ArenaUpdate {
    public Links _links;
    public Arena arena;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.initDirectFieldAccess();
  }

  @GetMapping("/")
  public String index() {
    return "Let the battle begin!";
  }

  @PostMapping("/**")
  public String index(@RequestBody ArenaUpdate arenaUpdate) {
    System.out.println(arenaUpdate);
    if (null == this.href) {
      this.href = arenaUpdate._links.self.href;
    }

    this.playerState = arenaUpdate.arena.state.get(this.href);
    this.direction = playerState.direction;
    
    if (playerState.wasHit) {
      healthNum += 1;
    } else {
      healthNum = 0;
    }

    if (playerState.score > curScore) {
      pointIncrease = true;
    } else {
      pointIncrease = false;
    }
    curScore = playerState.score;

    if (cmdSeq.empty()) {
      if (healthNum > 3) {
        moveRandom();
        fireTwice();
      } else if (pointIncrease) {
        fireTwice();
      } else {
        moveRandom();
        fireTwice();
      }
    }
    
    return cmdSeq.pop();
  }

  private void moveRandom() {
    cmdSeq.push("F");
    cmdSeq.push("F");
    String[] LF = new String[]{ "R", "L"};
    int i = new Random().nextInt(1);
    cmdSeq.push(LF[i]);
  }

  private void fireTwice() {
    cmdSeq.push("T");
    cmdSeq.push("T");
  }
}

