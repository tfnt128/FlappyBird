package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	//Declaração das variáveis
	SpriteBatch batch;
	Texture[] passaros;
	Texture fundo;
	Texture canoBaixo;
	Texture canoTopo;
	Texture gameOver;

	ShapeRenderer shapeRenderer;
	Circle circuloPassaro;
	Rectangle retanguloCanoTopo;
	Rectangle retanguloCanoBaixo;

	float larguraDispositivo;
	float alturaDispositivo;
	float variacao = 0;
	float gravidade = 2;
	float posicaoInicialVerticalPassaro;
	float posicaoCanoHorizontal;
	float posicaoCanoVertical;
	float espacoEntreCanos;
	Random random;
	int pontos = 0;
	int maiorPontuacao = 0;
	boolean passouCano = false;
	int estadoJogo = 0;
	float posicaoPassaroHorizontal;
	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMaiorPontuacao;

	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;

	Preferences preferencias;

	OrthographicCamera camera;
	Viewport viewport;
	final float VIRTUAL_WIDTH = 720;
	final float VIRTUAL_HEIGHT = 1280;

	@Override
	public void create () {
		iniciarTexturas(); // Inicializa as texturas do jogo
		iniciarObjetos(); // Inicializa os objetos do jogo
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}

	private void iniciarTexturas(){
		// Inicializa as texturas pelo internal path
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
	}

	private void iniciarObjetos(){
		batch = new SpriteBatch();
		random = new Random();

		larguraDispositivo = VIRTUAL_WIDTH; // Define a largura do dispositivo como a largura virtual
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVerticalPassaro = alturaDispositivo/2;
		posicaoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 350;

		textoPontuacao = new BitmapFont();
		// Configurações da fonte da pontuação
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(10);

		textoReiniciar = new BitmapFont();
		// Configurações da fonte do texto de reinício
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);

		textoMaiorPontuacao = new BitmapFont();
		// Configurações da fonte da maior pontuação
		textoMaiorPontuacao.setColor(Color.RED);
		textoMaiorPontuacao.getData().setScale(2);

		shapeRenderer = new ShapeRenderer(); // Inicializa um objeto para renderização de formas geométricas
		circuloPassaro = new Circle(); // Inicializa um círculo para representar o pássaro
		retanguloCanoBaixo = new Rectangle(); // Inicializa um retângulo para representar o cano inferior
		retanguloCanoTopo = new Rectangle(); // Inicializa um retângulo para representar o cano superior

		// Carrega os sons do jogo pelo internal path
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

		preferencias = Gdx.app.getPreferences("flappyBird"); // Inicializa um objeto de preferências para armazenar dados do jogo
		maiorPontuacao = preferencias.getInteger("maiorPontuação",0); // Obtém a maior pontuação salva nas preferências ou 0 se não houver

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2,0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	}

	private void verificarEstadoJogo(){

		// Verifica se houve um toque na tela
		boolean touchScreen = Gdx.input.justTouched();
		if( estadoJogo == 0){
			// Se o estado do jogo for 0 (inicial), verifica se houve toque na tela
			// Aplica uma gravidade negativa ao passaro, define o estado do jogo como 1 e reproduz um som de voo
			if( touchScreen){
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}
		}else if (estadoJogo == 1){
			// Se o estado do jogo for 1 (jogo em andamento), verifica se houve toque na tela
			// Aplica uma gravidade negativa ao passaro e reproduz um som de voo
			if(touchScreen){
				gravidade = -15;
				somVoando.play();
			}
			// Move a posição horizontal do cano para a esquerda
			// Verifica se o cano saiu da tela, e se sim, reposiciona-o e redefine a passagem do cano como false
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			if( posicaoCanoHorizontal < -canoTopo.getWidth()){
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoCanoVertical = random.nextInt(400) - 200;
				passouCano = false;
			}
			// Verifica se o passaro está acima do limite inferior da tela ou se houve toque na tela
			// Atualiza a posição vertical do passaro com base na gravidade
			if( posicaoInicialVerticalPassaro > 0 || touchScreen)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
			gravidade++;
		}else if( estadoJogo == 2){
			// Se o estado do jogo for 2 (game over), verifica se a pontuação atual é maior que a maior pontuação registrada
			// Atualiza a maior pontuação e armazena no SharedPreferences
			if (pontos> maiorPontuacao){
				maiorPontuacao = pontos;
					preferencias.putInteger("maiorPontuação", maiorPontuacao);
				preferencias.flush();
			}
			// Reposiciona o passaro para a posição inicial, reseta a pontuação e a gravidade
			posicaoPassaroHorizontal -= Gdx.graphics.getDeltaTime()*500;;

			if(touchScreen){
				// Verifica se houve toque na tela
				// Atualiza a posição horizontal do passaro
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoPassaroHorizontal = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo/2;
				posicaoCanoHorizontal = larguraDispositivo;
			}
		}
	}

	private void detectarColisoes(){
		// Define os círculos de colisão para o passaro, cano inferior e cano superior
		circuloPassaro.set(
				50 + posicaoPassaroHorizontal + passaros[0].getWidth()/2,
				posicaoInicialVerticalPassaro + passaros[0].getHeight()/2,
				passaros[0].getWidth()/2
		);
		retanguloCanoBaixo.set(
				posicaoCanoHorizontal,
				alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);
		retanguloCanoTopo.set(
				posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		// Verifica se houve colisão entre o passaro e os canos
		boolean collidedPipeTop = Intersector.overlaps(circuloPassaro, retanguloCanoTopo);
		boolean collidedPipeDown = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);

		// Se houver colisão e o estado do jogo for 1 (jogo em andamento), reproduz som de colisão e muda o estado do jogo para 2 (game over)
		if (collidedPipeTop || collidedPipeDown){
			if (estadoJogo == 1){
				somColisao.play();
				estadoJogo = 2;
			}
		}
	}

	private void desenharTexturas(){
		// Define a matriz de projeção da câmera para o batch de desenho
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// Desenha as texturas do fundo, passaro, canos e pontuação
		batch.draw(fundo,0,0,larguraDispositivo, alturaDispositivo);
		batch.draw(passaros[(int) variacao],
				50 + posicaoPassaroHorizontal, posicaoInicialVerticalPassaro);
		batch.draw(canoBaixo, posicaoCanoHorizontal,
				alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + posicaoCanoVertical);
		batch.draw(canoTopo, posicaoCanoHorizontal,
				alturaDispositivo/2 + espacoEntreCanos/2 + posicaoCanoVertical);
		textoPontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo/2,
				alturaDispositivo - 110);

		// Se o estado do jogo for 2 (game over), desenha as texturas de game over e pontuação recorde
		if(estadoJogo == 2){
			batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2,
					alturaDispositivo/2);
			textoReiniciar.draw(batch,
					"Toque para reiniciar!", larguraDispositivo/2 - 140,
					alturaDispositivo/2 - gameOver.getHeight()/2);
			textoMaiorPontuacao.draw(batch,
					"Seu recorde é: "+ maiorPontuacao + " pontos",
					larguraDispositivo/2-140, alturaDispositivo/2 - gameOver.getHeight());
		}
		batch.end();
	}

	public void validarPontos(){
		// Verifica se o cano passou da posição do pássaro e se ainda não foi contabilizado o ponto
		if( posicaoCanoHorizontal < 50-passaros[0].getWidth()){
			if (!passouCano){
				pontos++;
				passouCano = true;
				somPontuacao.play();
			}
		}

		// Atualiza a variável de variação do passaro baseado no tempo de renderização
		variacao += Gdx.graphics.getDeltaTime() * 10;

		// Reseta a variação do passaro quando ultrapassa o valor máximo de 3
		if (variacao > 3)
			variacao = 0;
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose () {
	}
}
