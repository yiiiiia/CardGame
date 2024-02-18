package structures.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	private int m_health;
	private int m_mana;
	private int m_maxHandCard;

	private ArrayList<Unit> m_allUnit;//可召唤的所有生物
	private  Map<Tile,Unit> m_unit;//现在场上的单位
	private List<Card> m_handCard;//现在的手牌
	private List<Card> m_cardsRemain;//牌库剩余的牌
	
	
	
	public Player(int health, int mana) {
		super();
		this.m_health = health;
		this.m_mana = mana;
		this.m_maxHandCard=6;
		
		m_allUnit=new ArrayList<Unit>();
		m_unit=new HashMap<Tile,Unit>();
		m_handCard=new ArrayList<Card>();
		m_cardsRemain=new ArrayList<Card>();
	}
	public Player(int health, int mana,int maxhandcard) {
		super();
		this.m_health = health;
		this.m_mana = mana;
		this.m_maxHandCard=maxhandcard;
		
		m_allUnit=new ArrayList<Unit>();
		m_unit=new HashMap<Tile,Unit>();
		m_handCard=new ArrayList<Card>();
		m_cardsRemain=new ArrayList<Card>();
	}
	
	public int getNumCard()
	{return this.m_handCard.size();}
	
	public Map<Tile,Unit> getUnit()
	{return this.m_unit;}
	
	public int getHealth() {
		return m_health;
	}
	public void setHealth(int health) {
		this.m_health = health;
	}
	public int getMana() {
		return m_mana;
	}
	public void setMana(int mana) {
		this.m_mana = mana;
	}
	
	public void addMana(int mana)
	{
		this.m_mana+=mana;
	}
	
	public void decreaseMana(int mana)
	{
		this.m_mana-=mana;
	}
	
	public  List<Card> getCard()
	{
		return this.m_handCard;
	}
	
	public void addUnit(Tile tile,Unit unit)
	{this.m_unit.put(tile, unit);}
	
	public void deleteUnit(Tile tile,Unit unit)
	{this.m_unit.remove(tile);}
	
	public void addHandCard(Card card)
	{if(this.m_handCard.size()>=this.m_maxHandCard)
	{return;}
		//手牌+1，牌库-1；
	m_handCard.add(card);
		m_cardsRemain.remove(card);
	}
	
	public void deleteHandCard(Card card)//在点击手牌那里实现
	{
		
		
	}
	
	public void useCard(Card card,Tile tile)//用卡
	{
		Unit curunit;
		int unitId=card.getId()>10?card.getId()-10:card.getId();
		curunit=this.m_allUnit.get(unitId);
		this.m_unit.put(tile, curunit);
		this.decreaseMana(card.getManacost());
		this.deleteHandCard(card);
	}
	
	public void addCardRemain(Card card)
	{
		m_cardsRemain.add(card);
	}
	
	public void deleteCardRemain(Card card)
	{
		m_cardsRemain.remove(card);
	}
	
}
