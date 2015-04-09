/*-----------------------------------------
 Memory Game
 Author: Hector Morales Piloni, MSc
 Date:   October 4, 2004
 Lastmod:October 13, 2004
 -----------------------------------------*/

import java.awt.*;
import java.net.*;
import java.awt.image.*;
import java.util.Random;

public class memory extends javax.swing.JApplet implements Runnable
{
	private Image board;
	private Image button_start;
	private Image cards[]; 
	private Image doubleBuffer;
	private Point coords[];
	private int nowPlaying;
	private int previousPlayer;
	private int counter;
	private int lastMove, currentMove;
	private int scorePlayer, scoreCPU;
	private boolean undoMove;
	private boolean CPUturn;
	private boolean showCard[];
	private boolean CPUmemory[];
	private boolean gameStarted;
	final private int ROWS = 4;
	final private int COLS = 6;
	final private int CPU = 0;
	final private int PLAYER = 1;
	final private int NONE = 2;
	private Thread m_thread;
	private Cursor handCursor,waitCursor,normalCursor;
  
    //Initializes applet
    public void init() 
	{
		initComponents();
		initCoords();
		initBoard();
		initCPUmemory();
                
		//sets board size
		this.setSize(652,537);
        
		//used to know what cards are visible
		showCard = new boolean[ROWS*COLS];
		for(int i=0; i<ROWS*COLS; i++)
			showCard[i] = false;
        
		handCursor = new Cursor(Cursor.HAND_CURSOR);
		waitCursor = new Cursor(Cursor.WAIT_CURSOR);
		normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(normalCursor);

		//load images
		board = getImage(getCodeBase(),"images/board.jpg");
		button_start = getImage(getCodeBase(),"images/button_start.jpg");
        
		nowPlaying = NONE;
		previousPlayer = NONE;
		CPUturn = false;
		counter = 0;
		lastMove = -1;
		scorePlayer = 0;
		scoreCPU = 0;
		undoMove = false;
		gameStarted = false;

		m_thread = new Thread(this);
		m_thread.start(); 
	}
    
	/*----------------------------------
	 Initializes the coordinates array
	 each one represent a position of 
	 an image in the board
	 ---------------------------------*/
	private void initCoords()
	{
		coords = new Point[ROWS*COLS];
        
		coords[0] = new Point(6,4);
		coords[1] = new Point(113,4);
		coords[2] = new Point(220,4);
		coords[3] = new Point(327,4);
		coords[4] = new Point(434,4);
		coords[5] = new Point(541,4);
		coords[6] = new Point(6,111);
		coords[7] = new Point(113,111);
		coords[8] = new Point(220,111);
		coords[9] = new Point(327,111);
		coords[10] = new Point(434,111);
		coords[11] = new Point(541,111);
		coords[12] = new Point(6,218);
		coords[13] = new Point(113,218);
		coords[14] = new Point(220,218);
		coords[15] = new Point(327,218);
		coords[16] = new Point(434,218);
		coords[17] = new Point(541,218);
		coords[18] = new Point(6,325);
		coords[19] = new Point(113,325);
		coords[20] = new Point(220,325);
		coords[21] = new Point(327,325);
		coords[22] = new Point(434,325);
		coords[23] = new Point(541,325);
	}
    
	/*-------------------------
	 Initializes the positions
	 of each image in the board
	 randomly
	--------------------------*/        
	private void initBoard()
	{
		Random rand = new Random();
		int img,i;
		int aux[];
        
		cards = new java.awt.Image[ROWS*COLS];

		//used to know how many times a 
		//random number has been used
		aux = new int[12];
		for(i=0; i<12; i++)
			aux[i] = 0;
        
		//make sure that a random number is used
		//at most twice... 
		for(i=0; i<ROWS*COLS; i++)
		{
			//get a number between 0 and 11
			img = java.lang.Math.abs(rand.nextInt()%12);
            
			//increment aux array to know how many times it has been used
			aux[img]++;
            
			//used already 2 times?
			if(aux[img]>2){
				do{
					//get another different random number
					img = java.lang.Math.abs(rand.nextInt()%12);
					aux[img]++;
				}while (aux[img]>2);
			}
            
			switch(img){
				case 0:
					cards[i] = getImage(getCodeBase(),"images/01.jpg");
					break;
				case 1:
					cards[i] = getImage(getCodeBase(),"images/02.jpg");
					break;
				case 2:
					cards[i] = getImage(getCodeBase(),"images/03.jpg");
					break;
				case 3:
					cards[i] = getImage(getCodeBase(),"images/04.jpg");
					break;
				case 4:
					cards[i] = getImage(getCodeBase(),"images/05.jpg");
					break;
				case 5:
					cards[i] = getImage(getCodeBase(),"images/06.jpg");
					break;
				case 6:
					cards[i] = getImage(getCodeBase(),"images/07.jpg");
					break;
				case 7:
					cards[i] = getImage(getCodeBase(),"images/08.jpg");
					break;
				case 8:
					cards[i] = getImage(getCodeBase(),"images/09.jpg");
					break;
				case 9:
					cards[i] = getImage(getCodeBase(),"images/10.jpg");
					break;
				case 10:
					cards[i] = getImage(getCodeBase(),"images/11.jpg");
					break;
				case 11:
					cards[i] = getImage(getCodeBase(),"images/12.jpg");
					break;
			}//switch
		}//for
	}
    
	/*-----------------------------
	Initializes the CPU memory
	at the beginning CPU does not
	know any position in the board
	------------------------------*/
	private void initCPUmemory()
	{
		CPUmemory = new boolean[ROWS*COLS];
        
		for(int i=0; i<ROWS*COLS; i++)
			CPUmemory[i] = false;
	}
    
    private void initComponents() 
	{
		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				memory.this.mouseClicked(evt);
			}
		});
	}

	private void mouseClicked(java.awt.event.MouseEvent evt) 
	{
		int col,row,pos;
        
		//used for control panel
		if(evt.getY()>431){
			if(evt.getX()>287 && evt.getX()<363)
				if(evt.getY()>440 && evt.getY()<478)
					if(!gameStarted)
						newGame();
			return;
		}

		if(nowPlaying == NONE || nowPlaying == CPU)
			return;
                
		//get applet's size
		Dimension d = getSize();
        
		//get what (col,row) user pressed
		//board is 6x4 tiles
		//ROWS+1 because of the extra row used for banners
		col = (evt.getX()*COLS)/d.width;
		row = (evt.getY()*(ROWS+1))/d.height;
        
		//get absolute position (0..23)
		pos = col + (row*COLS);
        
		//update CPU memory
		CPUmemory[pos] = true;
        
		//if already taken, do nothing
		if(showCard[pos])
			return;
        
		//if this is the second card PLAYER picked up then
		//check if it is the same as the previous one
		//if it's the same then update scores, else undo...
		counter++;
		if(counter == 2){
			counter = 0;
			currentMove = pos;
			if(cards[pos].equals(cards[lastMove]))
				scorePlayer++;
			else{
				undoMove = true;
				previousPlayer = PLAYER;
				nowPlaying = NONE;
				setCursor(waitCursor);
			}
		}
		else{
			//counter++;
			lastMove = pos;
		}

		showCard[pos] = true;
		if(gameFinished()){
			gameStarted = false;
			setCursor(normalCursor);
		}
		repaint();
	}
    
	public void paint(Graphics g)
	{
		Dimension d = getSize();
		BufferedImage doubleBuffer = new BufferedImage(d.width,d.height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) doubleBuffer.getGraphics();
        
		g2.drawString("CARGANDO...",d.width/2-10,d.height/2);
        
		g2.drawImage(board,0,0,this);
		for(int i=0; i<ROWS*COLS; i++){
			if(showCard[i])
				g2.drawImage(cards[i],(int)coords[i].getX(),(int)coords[i].getY(),this);
		}
		
        g2.setColor(new Color(196,139,46));
        g2.setFont(new Font("Arial",Font.BOLD,40));
		g2.drawString(" "+scorePlayer,115,474);
		g2.drawString(" "+scoreCPU,495,474);

        if(gameStarted){
	        g2.setColor(Color.BLACK);
	        g2.setFont(new Font("Arial",Font.BOLD,28));
			g2.drawString("TURNO:",15,520);
			if(previousPlayer == CPU)
				g2.drawString("JUGADOR",254,520);
			if(previousPlayer == PLAYER)
				g2.drawString("CPU",300,520);
		}
        else{
	        g2.setColor(Color.BLACK);
	        g2.setFont(new Font("Arial",Font.BOLD,24));
			g2.drawString("Presiona el botón para comenzar",120,518);
            g2.drawImage(button_start,287,440,this);
		}
		g2.setFont(new Font("Arial",Font.PLAIN,10));
		//g2.drawString("Programmed by: Héctor Morales Piloni, MSc. - http://piloni.raised.us/cv",150,530);
		g.drawImage(doubleBuffer,0,0,this);
    }

    public void newGame()
	{
		Random rand = new Random();
        
        //hide every card
        for(int i=0; i<ROWS*COLS; i++)
            showCard[i] = false;
        repaint();
               
        initBoard();
        initCPUmemory();
        
        //clear scores
        scoreCPU = 0;
        scorePlayer = 0;
        
        gameStarted = true;
        
        //selects a new player randomly
        if((java.lang.Math.abs(rand.nextInt()%1))==PLAYER){
            nowPlaying = PLAYER;
            setCursor(handCursor);
        }
        else{
            nowPlaying = CPU;
            setCursor(waitCursor);
            CPUmove();
        }        
    }
    
	/*-------------------------------
	CPU makes a first random move
	for the second move it checks
	if the card from the 1st move 
	is in its memory...
	-------------------------------*/
    public void CPUmove()
	{
        int pos;
        boolean taken = false;
        boolean inMemory = false;
        Random rand = new Random();

        do{
            //select a random position in the board
            pos = java.lang.Math.abs(rand.nextInt()%(ROWS*COLS));

            //check if that position has been taken already
            if(showCard[pos])
                taken = true;
            else
                taken = false;            
        }while(taken);        
        
        //if this is the second card then
        //check if it is the same as the previous one
        //if it's the same then update scores, else undo move
        counter++;
        if(counter == 2){
            counter = 0;
            if(cards[pos].equals(cards[lastMove])){
                scoreCPU++;
                //CPU still has the turn to move
                CPUturn = true;
            }
            else{
                //check if CPU has the pair in its memory
                for(int i=0; i<ROWS*COLS; i++){
                    //for each card CPU knows...
                    if(CPUmemory[i]){
                        //their positions are different?
                        if(i != lastMove){
                            //are the same card?
                            if(cards[lastMove].equals(cards[i])){
                                pos = i;
                                inMemory = true;
                                break;
                            }
                        }
                    }
                }//for
                
                if(inMemory){
                    scoreCPU++;
                    //CPU still has the turn to move
                    CPUturn = true;
                }
                else{
                    CPUturn = false;
                    undoMove = true;
                    previousPlayer = CPU;
                    nowPlaying = NONE;
                }
            }
            currentMove = pos;
        }
        else{
            //CPU still has the turn to move
            CPUturn = true;
            lastMove = pos;
        }

        //update CPU memory
        CPUmemory[pos] = true;

        showCard[pos] = true;
        if(gameFinished()){
            gameStarted = false;
            setCursor(normalCursor);
        }
        repaint();
    }
    
	/*---------------------------
	just checks if the game
	has ended (i.e. every card
	has been shown)
	---------------------------*/
    public boolean gameFinished()
	{
        boolean finished = true;
        
        for(int i=0; i<ROWS*COLS; i++){
            if(!showCard[i]){
                finished = false;
                break;
            }//if
        }//for
            
        return finished;
    }
    
    public void run()
	{       
        Thread actualThread = Thread.currentThread();
        while(actualThread == m_thread){
            try{
                //if undoMove, then sleep a little set to false
                //the last 2 movements and repaint, this way those
                //cards will be switched off
                if(undoMove){
                    Thread.sleep(1000);
                    showCard[lastMove] = false;
                    showCard[currentMove] = false;
                    undoMove = false;
                    repaint();
                    if(previousPlayer == PLAYER){
                        nowPlaying = CPU;
                        CPUmove();
                    }
                    else{
                        nowPlaying = PLAYER;
                        setCursor(handCursor);
                    }
                }//if undoMove
                
                //If CPU has the turn to move, delay a little
                //and call its function
                if(CPUturn){
                    Thread.sleep(500);
                    CPUmove();
                }//if CPUturn      
                Thread.sleep(40);
            } catch(InterruptedException ex){}            
        }//while
    }    
}
