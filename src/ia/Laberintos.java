package ia;

import processing.core.PApplet;
import processing.core.PFont;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Random;


/**
 *
 * @author berna
 */
public class Laberintos extends PApplet {

    PFont fuente;  // Fuente para mostrar texto en pantalla
    
    // Propiedades del modelo del laberinto.
    int alto = 20;         // Altura (en celdas) de la cuadricula.
    int ancho = 30;        // Anchura (en celdas) de la cuadricula.
    int celda = 30;          // Tamanio de cada celda cuadrada (en pixeles).
    ModeloMaze modelo;  //Modelo del laberinto
    

    @Override
    public void setup(){
        frameRate(25);
        size( ancho*celda, (alto*celda)+32);
        background(255);
        fuente = createFont("Arial",12,true);
        modelo = new ModeloMaze(ancho, alto, celda);
     }
    
    
    
    /**
     * Pintar las cuadriculas del mundo.
     */
    @Override
    public void draw() {
        
        for(int i = 0; i < alto; i++)
          for(int j = 0; j < ancho; j++){
            if(modelo.mundo[i][j].visitado)              
                fill(244, 208, 63);            
            else
                fill(204,209,209);
              
            noStroke();
            rect(j*celda, i*celda, celda, celda);             
            display(i,j); 
          }

       //Pintando el explorador
        fill(125, 60, 152);
        noStroke();
        rect((modelo.t.posX)*celda,(modelo.t.posY)*celda,celda,celda);
                

        // Pintar informacion del modelo en la parte inferior de la ventana.
        fill(50);
        rect(0, alto*celda, (ancho*celda), 32);
        fill(255);
        textFont(fuente,10);
        text("Cuadricula: " + modelo.ancho + " x " + modelo.alto, 5, (alto*celda)+12);
        
        modelo.makeMaze();        
    }
    
    
    
    /**
     * dibuja la cuadricula utilizando el metodo <line()> para dibujar lineas y facilitar el borrado al momento de crear los caminos del laberinto
     * @param i coordenada Y
     * @param j  coordenada X
     */
     public void display(int i, int j){
         boolean []pared=modelo.mundo[i][j].walls;
         stroke(0);
          if(pared[0])
              line(j*celda,i*celda,(j*celda)+celda,i*celda);
          if(pared[1])
            line((j*celda)+celda,i*celda,(j*celda)+celda,(i*celda)+celda);    
          if(pared[2])
            line(j*celda,(i*celda)+celda,(j*celda)+celda,(i*celda)+celda);
          if(pared[3])
             line(j*celda,i*celda,j*celda,(i*celda)+celda); 
      }
     
     
     
     
    // --- Clase Celda ---
    /**
     * Representación de cada celda de la cuadrícula.
     */
    class Celda{
      int celdaX, celdaY;
      boolean visitado=false;
      boolean walls[]={true,true,true,true};
      Stack <Celda> padre; //cada vez que pasan por esta celda hace un <push()> de su padre 
      
      
      /** Constructor de una celda
        @param celdaX Coordenada en x
        @param celdaY Coordenada en y
        @param estado True para activada (espacio con astilla), false en otro caso.
      */
      Celda(int celdaY, int celdaX, boolean estado){
        this.celdaX = celdaX;
        this.celdaY = celdaY;
        this.visitado = estado;
        padre=new Stack<Celda>();
      } 
      
    }
    
    
    
    // --- Clase Traveler ---
    /**
     * Los atributos del encargado de explorar del laberinto 
     */
    class Traveler{
      int posX, posY;  // Coordenadas de la posicion de la termita
      int direccion;   // Valor entre 0 y 3 para indicar dirección de movimiento
      

      /** Constructor de una termita
        @param posX Indica su posicion en el eje X
        @param posY Indica su posicion en el eje Y
        
           --------
           | ~| 0 | ~ |
           |-------|
           | 3|   | 1 |
           |-------|
           | ~| 2| ~ |
           --------
      */
      Traveler(int posY, int posX){
        this.posX = posX;
        this.posY = posY;        
      }
      
      
    }

    

    // --- Clase ModeloMaze ---
    /**
     * Crea el modelo del laberinto para que tenga la funcion deseada incluyendo al explorador
     */
    class ModeloMaze{
      int ancho, alto;  // Tamaño de celdas a lo largo y ancho de la cuadrícula.
      int tamanio;  // Tamaño en pixeles de cada celda.
      Celda[][] mundo;  // Mundo de celdas donde habitan las astillas.
      Random rnd = new Random();  // Auxiliar para darnos las direcciones aleatorias
      Traveler t;
      ArrayList<Integer> libre; //lista con los posibles caminos [0,1,2,3] a donde puede moverse 
      
      
      
      
      /** Constructor del modelo
        @param ancho Cantidad de celdas a lo ancho en la cuadricula.
        @param alto Cantidad de celdas a lo largo en la cuadricula.
        @param tamanio Tamaño (en pixeles) de cada celda cuadrada que compone la cuadricula.
        */
      ModeloMaze(int ancho, int alto, int tamanio){
        this.ancho = ancho;
        this.alto = alto;
        this.tamanio = tamanio;
        //this.generacion = 0;
        //Inicializar mundo (usar densidad)
        mundo = new Celda[alto][ancho];
        for(int i = 0; i < alto; i++){
          for(int j = 0; j < ancho; j++)
            mundo[i][j] = new Celda(i,j,false);
        }
        t=new Traveler(rnd.nextInt(alto),rnd.nextInt(ancho));//alto, ancho
      }

      
      
      /** Mueve al explorador segun la direccion dada.
       * toma en cuenta si se encuentra en alguna orilla o si se movera a una celda que  ya esta visitada
        @param direccion La direccion en la que se desea mover la termita (con valor entre 0 y 7).
      */
      void moverTraveler(int direccion){
         
          mundo[t.posY][t.posX].visitado=true;
          
        switch(direccion) {
            
          case 0: 
                  if(t.posY-1<0){//orilla
                     break; 
                  }else if(estaVisitado(direccion)){//moverse a un visitado                    
                     break;
                  }else{
                      mundo[t.posY][t.posX].walls[0]=false;
                      t.posY--;
                      mundo[t.posY][t.posX].walls[2]=false;
                      mundo[t.posY][t.posX].padre.push(new Celda(t.posY+1,t.posX,true));                     
                  }
                  break;
                    
          case 1: if(t.posX+1>=ancho){//orilla
                     break; 
                  }else if(estaVisitado(direccion)){//moverse a un visitado                     
                     break;
                  }else{
                      mundo[t.posY][t.posX].walls[1]=false;
                      t.posX++;
                      mundo[t.posY][t.posX].walls[3]=false;
                      mundo[t.posY][t.posX].padre.push(new Celda(t.posY,t.posX-1,true));
                  }
                   break;
                                     
          case 2:   
                  if(t.posY+1>=alto){//orilla
                     break; 
                  }else if(estaVisitado(direccion)){//moverse a un visitado                
                     break;
                  }else{
                      mundo[t.posY][t.posX].walls[2]=false;
                      t.posY++;
                      mundo[t.posY][t.posX].walls[0]=false;
                      mundo[t.posY][t.posX].padre.push(new Celda(t.posY-1,t.posX,true));
                  }
                  break;
          case 3:
                  if(t.posX-1<0){//orilla
                     break; 
                  }else if(estaVisitado(direccion)){//moverse a un visitado                     
                     break;
                  }else{
                      mundo[t.posY][t.posX].walls[3]=false;
                      t.posX--;
                      mundo[t.posY][t.posX].walls[1]=false;
                      mundo[t.posY][t.posX].padre.push(new Celda(t.posY,t.posX+1,true));
                  }                                     
                   break;
                   
         
            }   
        
      }

      
      
      /**
       * Metodo que es invocado cuando no tiene a donde moverse y elige regresar a un paso anterior 
       */
      void back(){
          
          if(mundo[t.posY][t.posX].padre !=null){
          Celda p=mundo[t.posY][t.posX].padre.pop();    
          mundo[t.posY][t.posX].visitado=true;
          t.posX=p.celdaX;
          t.posY=p.celdaY;
        
          }
      }
      
      
      
      
      /**
       * Metodo para revisar si nuestro explorador no pude moverse ya que sus tres direcciones  estan ocupadas o se encuentra en la orilla
       * @return res true si estan todos visitados o false si existe un lugar donde pueda moverse
       * Ademas configura la lista de caminos libres 
       */
      boolean vecinosVisitados(){
          boolean res=true;
          boolean tem[]={false,false,false,false};
          libre=new ArrayList<Integer>();
          for(int j=0;j<4;j++)
              libre.add(j);
 
          if( t.posY-1<0 || (mundo[t.posY-1][t.posX].visitado) ){//direccion 0
              tem[0]=true;
              libre.remove(libre.contains(0));
          }
          if(t.posX+1>=ancho || (mundo[t.posY][t.posX+1].visitado) ){//direccion 1
              tem[1]=true;
               libre.remove(libre.contains(1));
          }
          if(t.posY+1>=alto || (mundo[t.posY+1][t.posX].visitado) ){//direccion 2
              tem[2]=true;
               libre.remove(libre.contains(2));
          }
          if(t.posX-1<0 || (mundo[t.posY][t.posX-1].visitado) ){//direccion 3
              tem[3]=true;
               libre.remove(libre.contains(3));
          }
          
          for(int i=0;i<tem.length;i++){
              if(!tem[i])
                  return false;
          }
          
      return res;
      }
      
      
      
      /**
       * Revisa si todo el tablero ya ha sido visitado. 
       * @return true si ya fue todo el tablero visitado
       */
      boolean todosVisitados(){
          
          for(int i=0;i<alto;i++){
              for(int j=0;j<ancho;j++){
                  if(!mundo[i][j].visitado)
                      return false;
              }
          }
          return true;
      }
      
      
      
      /**
       * Metodo que verifica si a la celda donde se movera ya ha sido visitada
       * @param dir la direccion a donde  se movera
       * @return true si  a donde se movera ya fue visitada
       */
      boolean estaVisitado(int dir){
        
          Traveler r=new Traveler(t.posY,t.posX);
          if(dir ==0)
              r.posY--;
          if(dir== 1)
              r.posX++;
          if(dir==2)
              r.posY++;
          if(dir==3)
              r.posX--;
          
          //moverTraveler(dir);
          return mundo[r.posY][r.posX].visitado;
              
      }
      
      
      /**
       * Realiza el movimiento de nuestro explorador dentro del tablero creando los caminos de nuestro laberinto
       */
      void makeMaze(){         

            int dir=rnd.nextInt(4);
              
             if(!todosVisitados()){                  
                if(vecinosVisitados())
                    back();
                moverTraveler(dir);
             }               
      }
    }
    
    
    static public void main(String args[]) {
       
        PApplet.main(new String[] { "ia.Laberintos" });
    }
}

