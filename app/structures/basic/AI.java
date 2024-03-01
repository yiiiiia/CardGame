package draft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

public class AI extends Player {




public AI(int health, int mana)
{super(health,mana);}

public void aiAction(GameState gameState,ActorRef out, JsonNode message)
{this.useSpellCard(gameState,out,message);
this.useCreatureCard(gameState,out,message);
this.moveOrAttack(gameState,out,message);
	}
//AI策略先用效果牌，直接先减员。stun卡作用攻击力最高的单位。治疗卡用于已损失生命值最大的单位,或者攻击力最高的单位，或者最易收到攻击的单位。攻击卡用于当前生命值小于
//等于2的，如果没有，攻击攻击力最高的。
//召唤类牌蓝耗从小到大，召唤位置优先Avater附近，rush卡尽量靠近地方单位
//攻击和移动：优先攻击最近的
public List<Card> haveSpell()
{List<Card> spell=new ArrayList<Card>();
for(Card cur:this.getCard())
{if(!cur.isCreature())
{spell.add(cur);
	}
	}
	return spell;
	}


//使用spell卡
public void useSpellCard(GameState gameState,ActorRef out,JsonNode message)
{List<Card> spellCard=this.haveSpell();
if(spellCard==null)
{return;
	}
else
{for(Card cur:spellCard)
	{String curname=cur.getCardname();
	if(this.getMana()>=cur.getManacost())
	{switch(curname)
	{
	case "Sundrop Elixir":useSundrop(cur,gameState, out, message);//改一下使用时机
		break;
	case "True Strike":useTrueStrike(cur,gameState, out, message);
		break;
	case "Beam Shock":useStun(cur,gameState, out, message);//后续升级成攻击一次后使用bean shock
		break;
	}
	}
	}
	}
	
}


//Beam Shock卡的使用
public void useStun(Card card,GameState gameState,ActorRef out, JsonNode message) 
{Tile attackMax=null;
for(Tile cur:gameState.getUserPlayer().getAllUnits().keySet())
{if(!gameState.getUserPlayer().getAllUnits().get(cur).getStunned()&&gameState.getUserPlayer().getAllUnits().get(cur).getAttack()>gameState.getUserPlayer().getAllUnits().get(attackMax).getAttack())
{attackMax=cur;
	}
	}
//card.stun(Unit unit),这个stun是卡内定义的攻击方法

Card c1=new BeamShockCard();
if(c1.performSpell(out, gameState ,gameState.getUserPlayer().getAllUnits().get(cur)))
{c1.highlightTiles( out,  gameState);
	//播放死亡动画，这个应该在卡里方法检测}
	}
this.deleteHandCard(card);
}


//Sundrop卡的使用
public void useSundrop(Card card,GameState gameState,ActorRef out, JsonNode message)
{Tile attackMax=null;
int maxLostHp=0;
for(Tile cur:gameState.getUserPlayer().getAllUnits().keySet())
{int nowLostHp=gameState.getUserPlayer().getAllUnits().get(cur).getMaxHealth()-gameState.getUserPlayer().getAllUnits().get(cur).getCurHealth();
	if(maxLostHp<nowLostHp)
{attackMax=cur;
maxLostHp=nowLostHp;
	}
	}
//card.Sundrop(Unit unit),这个stun是卡内定义的方法
Card c2=new SundropElixirCard();
if(c2.performSpell(out, gameState ,gameState.getUserPlayer().getAllUnits().get(cur)))
{c2.highlightTiles( out,  gameState);
	//播放死亡动画，这个应该在卡里方法检测}
	}
this.deleteHandCard(card);
this.decreaseMana(1);
	
}

//TrueStrike卡的使用
public void useTrueStrike(Card card,GameState gameState,ActorRef out, JsonNode message)
{Tile healthMin=null;
for(Tile cur:gameState.getUserPlayer().getAllUnits().keySet())
{if(gameState.getUserPlayer().getAllUnits().get(cur).getCurHealth()<gameState.getUserPlayer().getAllUnits().get(healthMin).getCurHealth())
{healthMin=cur;
	}
	}


Card c3=new TrueStrikeCard();
if(c3.performSpell(out, gameState ,gameState.getUserPlayer().getAllUnits().get(cur)))
{c3.highlightTiles( out,  gameState);
	//播放死亡动画，这个应该在卡里方法检测}
	}
this.deleteHandCard(card);
this.decreaseMana(1);
	
}


//蓝耗从小到大，比较器
class PersonComparator implements Comparator<Card> {
    @Override
    public int compare(Card c1, Card c2) {
        return Integer.compare(c1.getManacost(), c2.getManacost());
    }
}

//使用召唤类卡片
public void useCreatureCard(GameState gameState,ActorRef out,JsonNode message)
{if(this.getMana()==0)
{
	return;}
else
{
	Collections.sort(this.getCard(), new PersonComparator());
	for(Card cur:this.getCard())
	{if(this.getMana()<cur.getManacost())
	{
		return;
	}
		Tile tile=placeableArea();
		if(tile!=null)
		{
			cur.summonUnit(out,gameState,tile.getTilex(),tile.getTiley());
		}
		//summon(Tile tile)召唤
	}
}
	
}

//判断是否是可放置区域
public boolean isPlaceBle(int x,int y,GameState gameState)
{if(x<0||x>8||y<0||y>4||gameState.getUserPlayer().getAllUnits().containsKey(gameState.getTileByPos(x, y)))//这里得再加AI类的unit判断
{return false;}
return true;
}


//返回可召唤生物的Tile
public Tile placeableArea(GameState gameState)//后期可以加一点放在哪里的策略
{for(Tile cur:this.getAllUnits().keySet())
{
	int x=cur.getTilex()-1;
int y=cur.getTiley()-1;
	for(int i=0;i<3;i++)
{for(int j=0;j<3;j++)
{if(isPlaceBle(x+i,y+j,gameState))
{
	return gameState.getTileByPos(x+i,y+j);
}

}
}
	
	}
return null;
	}



//攻击或者移动，如果没有攻击目标，则朝着最近目标移动，后续再优化
public void moveOrAttack(GameState gameState,ActorRef out, JsonNode message)
{for(Tile cur:this.getAllUnits().keySet())
{Unit aiUnit = gameState.getAiPlayer().getAllUnitsByTile(cur);
Unit userUnit=gameState.getUserPlayer().getAllUnits().get(getClosestTile(cur));
List<Tile> tilesAccessible = gameState.getTilesAccessible(aiUnit);
	
	//第一个分支，相邻单位可以攻击
	if (gameState.unitsAdjacent(aiUnit, userUnit)) {
		// user unit and ai unit are adjacent
		aiUnit.unitAttack(out, gameState, userUnit);
		if (userUnit.getHealth() > 0) {
			// perform counter attack
			userUnit.unitAttack(out, gameState, aiUnit);
		}
		gameState.clearActiveUnit();
		break;
	}
	//相邻单位无可攻击单位
	else
	{Tile targetTile = null;
	for (Tile tile : tilesAccessible) {
		if (gameState.tilesAdjacent(tile, getClosestTile(cur,gameState))) {
			targetTile = tile;
			break;
		}
	}
	//移动之后也无可攻击单位，超最近目标移动
	if (targetTile == null) {
		// cannot perform move + attack
		int distancex=getClosestTile(cur).getTilex()-cur.getTilex();
		int distancey=getClosestTile(cur).getTiley()-cur.getTiley();
		if(Math.abs(distancex)>Math.abs(distancey))
		{
			aiUnit.unitMove(out, gameState, gameState.getTileByPos(cur.getTilex()+Integer.signum(distancex)*2,cur.getTiley()));
		}
		else if(Math.abs(distancex)==Math.abs(distancey))
		{
			aiUnit.unitMove(out, gameState, gameState.getTileByPos(cur.getTilex()+Integer.signum(distancex),cur.getTiley()+Integer.signum(distancey)));
		}
		else
		{
			aiUnit.unitMove(out, gameState, gameState.getTileByPos(cur.getTilex(),cur.getTiley()+Integer.signum(distancey)*2));
		}
		break;
		}
	//移动后攻击
	else {
		String reason = gameState.unitCanMove(aiUnit);
		if (reason != null) {
			
			break;
		}
		Action action = new Action() {
			@Override
			public void doAction(ActorRef out, GameState gameState) {
				performAttackAndCounterAttack(out, gameState, aiUnit, userUnit);
				gameState.clearActiveUnit();

			}
		};
	
	}
	
		gameState.setPendingAction(action);
		// clear current tile effects
		// tell unit to move
		aiUnit.unitMove(out, gameState, targetTile);
	}
	}
}

//判断是否有攻击目标,后面要改，目前没用上；
public boolean haveAttack(Tile tile,GameState gameState,ActorRef out, JsonNode message)
{return true;
	}

//相邻单位
public List<Unit> getAdjacenUnit(Tile tile,GameState gameState)
{List<Unit> adjacenUnit=new ArrayList<Unit>();
int x=tile.getTilex();
int y=tile.getTiley();
for(int i=0;i<3;i++)
{
	for(int j=0;j<3;j++)
		{
		if(gameState.getUserPlayer().getAllUnits().containsKey(gameState.getTileByPos(x-1+i,y-1+i)))
		{
			adjacenUnit.add(gameState.getUserPlayer().getAllUnits().get(gameState.getTileByPos(x-1+i,y-1+i)));
		}
		}
	}
return adjacenUnit;
	}

//找最近的攻击单位
public Tile getClosestTile(Tile aitile,GameState gameState)//保证第一轮走完一周，判断有没有provoke卡
{  int rows = 9;//行数
int cols = 5;//列数
boolean[][] visited = new boolean[rows][cols];
int k=0;//计数器，用来判断第一周是否有provoke卡
Queue<int[]> queue = new LinkedList<>();
queue.offer(new int[]{aitile.getTilex(), aitile.getTiley()});
visited[aitile.getTilex()][aitile.getTiley()] = true;
Tile targetTile=null;
int[][] directions = {{-1, 0},{-1,-1}, {1, 0},{1,1}, {0, -1},{-1,-1}, {0, 1},{1,1}};//后续改下这个矩阵，从左开始比较好


while (!queue.isEmpty()) {
    int[] currentCell = queue.poll();
    int currentRow = currentCell[0];
     int currentCol = currentCell[1];

    if (k>8&&gameState.getUserPlayer().getAllUnits().containsKey(gameState.getTileByPos(currentRow,currentCol))) {
        return gameState.getTileByPos(currentRow,currentCol); // 找到最近的tile
    }
    else
    {if(k==8&&targetTile!=null)
    {
    	return targetTile;
    }
    if(gameState.getUserPlayer().getAllUnits().containsKey(gameState.getTileByPos(currentRow,currentCol))&&gameState.getUserPlayer().getAllUnits().get(gameState.getTileByPos(currentRow,currentCol)).getName().equals("Rock Pulveriser"))
    {
    	return gameState.getTileByPos(currentRow,currentCol);
    }
    if(gameState.getUserPlayer().getAllUnits().containsKey(gameState.getTileByPos(currentRow,currentCol))) 
    {
    	targetTile=gameState.getTileByPos(currentRow,currentCol);
    }
    
    	
    }
    k++;

    for (int[] direction : directions) {
        int newRow = currentRow + direction[0];
        int newCol = currentCol + direction[1];

        if (isValid(newRow, newCol, rows, cols) && !visited[newRow][newCol]) {
            queue.offer(new int[]{newRow, newCol});
            visited[newRow][newCol] = true;
        }
    }
}

return null; // 没有找到包含用户unit的tile
}

//判断找的最近位置是否合理
private static boolean isValid(int row, int col, int rows, int cols) {
return row >= 0 && row < rows && col >= 0 && col < cols;
}
	}

