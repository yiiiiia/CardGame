package draft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

public class AI extends Player {

GameState gameState;
public AI(int health, int mana)
{super(health,mana);}

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
public void useSpellCard()
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
	case "Sundrop Elixir":useSundrop(cur);
		break;
	case "True Strike":useTrueStrike(cur);
		break;
	case "Beam Shock":useStun(cur);
		break;
	}
	}
	}
	}
	
}


//Beam Shock卡的使用
public void useStun(Card card) 
{Tile attackMax=null;
for(Tile cur:gameState.players[0].getUnit().keySet())
{if(gameState.players[0].getUnit().get(cur).getAttack()>gameState.players[0].getUnit().get(attackMax).getAttack())
{attackMax=cur;
	}
	}
//card.stun(Unit unit),这个stun是卡内定义的攻击方法
if(gameState.players[0].getUnit().get(attackMax).getHealth()<=0)
{
	//播放死亡动画，这个应该在卡里方法检测}
	}
this.deleteHandCard(card);
}


//Sundrop卡的使用
public void useSundrop(Card card)
{Tile attackMax=null;
for(Tile cur:gameState.players[10].getUnit().keySet())
{if(gameState.players[1].getUnit().get(cur).getAttack()>gameState.players[0].getUnit().get(attackMax).getAttack())
{attackMax=cur;
	}
	}
//card.Sundrop(Unit unit),这个stun是卡内定义的方法

this.deleteHandCard(card);
this.decreaseMana(1);
	
}

//TrueStrike卡的使用
public void useTrueStrike(Card card)
{Tile healthMin=null;
for(Tile cur:gameState.players[0].getUnit().keySet())
{if(gameState.players[0].getUnit().get(cur).getCurHealth()<gameState.players[0].getUnit().get(healthMin).getCurHealth())
{healthMin=cur;
	}
	}
//card.Sundrop(Unit unit),这个stun是卡内定义的方法
//unit=gameState.players[0].getUnit().get(healthMin)；

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
public void useCreatureCard()
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
		//summon(Tile tile)召唤
	}
}
	
}
public boolean isPlaceBle(int x,int y,GameState gameState)
{if(x<0||x>8||y<0||y>4||gameState.players[0].getUnit().containsKey(gameState.boardTile[x][y]))//这里得再加AI类的unit判断
{return false;}
return true;
}
public Tile placeableArea()//后期加avatar附近没放置区域的代码
{for(Tile cur:this.getUnit().keySet())
{if(this.getUnit().get(cur).getId()==1)//假设avatar的id是1
{	int x=cur.getTilex()-1;
int y=cur.getTiley()-1;
	for(int i=0;i<3;i++)
{for(int j=0;j<3;j++)
{if(isPlaceBle(x+i,y+j,gameState))
{
	return gameState.boardTile[x+i][y+j];
}

}
}
	}
	}
return null;
	}

//攻击或者移动，如果没有攻击目标，则朝着最近目标移动，后续再优化
public void moveOrAttack()
{for(Tile cur:this.getUnit().keySet())
{Unit aiUnit = gameState.getAiPlayer().getUnitByTile(cur);
Unit userUnit=gameState.players[0].getUnit().get(getClosestTile(cur));
List<Tile> tilesAccessible = gameState.getTilesAccessible(aiUnit);
	if(haveAttack(cur))
{//攻击
	
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
	else
	{Tile targetTile = null;
	for (Tile tile : tilesAccessible) {
		if (gameState.tilesAdjacent(tile, getClosestTile(cur))) {
			targetTile = tile;
			break;
		}
	}
	if (targetTile == null) {
		// cannot perform move + attack
		break;
		String reason = gameState.unitCanMove(aiUnit);
		if (reason != null) {
			
			break;
		}
		Action action = new Action() {
			@Override
			public void doAction(ActorRef out, GameState gameState) {
				aiUnit.unitAttack(out, gameState, userUnit);
				if (aiUnit.getHealth() > 0) {
					// perform counter attack
					userUnit.unitAttack(out, gameState, aiUnit);
					gameState.clearActiveUnit();
				}
			}
		};
		gameState.setPendingAction(action);
		// clear current tile effects
		
		// tell unit to move
		aiUnit.unitMove(out, gameState, targetTile);

	}


	}

	
	

	}
//无攻击目标，向目标靠近
else {
	//靠近目标
	int distancex=getClosestTile(cur).getTilex()-cur.getTilex();
	int distancey=getClosestTile(cur).getTiley()-cur.getTiley();
	if(Math.abs(distancex)>Math.abs(distancey))
	{
		aiUnit.unitMove(out, gameState, gameState.boardTile[cur.getTilex()+Integer.signum(distancex)*2][cur.getTiley()]);
	}
	else if(Math.abs(distancex)==Math.abs(distancey))
	{
		aiUnit.unitMove(out, gameState, gameState.boardTile[cur.getTilex()+Integer.signum(distancex)][cur.getTiley()+Integer.signum(distancey)]);
	}
	else
	{
		aiUnit.unitMove(out, gameState, gameState.boardTile[cur.getTilex()][cur.getTiley()+Integer.signum(distancey)*2]);
	}
	
}
	}
	}

//判断是否有攻击目标,后面要改；
public boolean haveAttack(Tile tile)
{return true;
	}

//相邻单位
public List<Unit> getAdjacenUnit(Tile tile)
{List<Unit> adjacenUnit=new ArrayList<Unit>();
int x=tile.getTilex();
int y=tile.getTiley();)
for(int i=0;i<3;i++)
{
	for(int j=0;j<3;j++)
		{
		if(gameState.players[0].getUnit().containsKey(gameState.boardTile[x-1+i][y-1+i]))
		{
			adjacenUnit.add(gameState.players[0].getUnit().get(gameState.boardTile[x-1+i][y-1+i]));
		}
		}
	}
return adjacenUnit;
	}

//找最近的攻击单位
public Tile getClosestTile(Tile aitile)
{  int rows = 9;//行数
int cols = 5;//列数
boolean[][] visited = new boolean[rows][cols];

Queue<int[]> queue = new LinkedList<>();
queue.offer(new int[]{aitile.getTilex(), aitile.getTiley()});
visited[aitile.getTilex()][aitile.getTiley()] = true;

int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};//后续改下这个矩阵，从左开始比较好


while (!queue.isEmpty()) {
    int[] currentCell = queue.poll();
    int currentRow = currentCell[0];
     int currentCol = currentCell[1];

    if (gameState.players[0].getUnit().containsKey(gameState.boardTile[currentRow][currentCol])) {
        return gameState.boardTile[currentRow][currentCol]; // 找到最近的tile
    }

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

