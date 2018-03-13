/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.stefanini.control;

import br.com.stefanini.control.dao.CustoDAO;
import br.com.stefanini.control.dao.ModuloDAO;
import br.com.stefanini.control.dao.PacoteDAO;
import br.com.stefanini.control.dao.ProjetoDAO;
import br.com.stefanini.model.entity.Atividade;
import br.com.stefanini.model.entity.Custo;
import br.com.stefanini.model.entity.Modulo;
import br.com.stefanini.model.entity.Pacote;
import br.com.stefanini.model.entity.Projeto;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * FXML Controller class
 *
 * @author lucas
 */
public class PainelDeControleController extends ControllerBase implements Initializable {

    @FXML
    private AnchorPane apPrincipal;

    @FXML
    private Spinner<Integer> spAno;

    @FXML
    private GridPane gpMeses;

    @FXML
    private ScrollPane spContainer;
    @FXML
    private ProgressIndicator piLoader;
    private GerenciadorDeJanela gerenciadorDeJanela;

    @FXML
    private AnchorPane apMeses;

    @FXML
    private ComboBox<Projeto> filtroProjeto;

    @FXML
    private ComboBox<Modulo> filtroModulo;

    @FXML
    private ComboBox<Pacote> filtroPacote;

    @FXML
    private MenuBar mbGerenciar;
    
    @FXML
    private Menu menu;
    
    private Stage stage;
    ArrayList<Atividade> atividades = new ArrayList<>();

    Map<String, Object> params = new HashMap<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Calendar calendar = Calendar.getInstance();
        spAno.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, calendar.get(Calendar.YEAR)));
        apPrincipal.sceneProperty().addListener((ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) -> {
            params = (Map<String, Object>) apPrincipal.getUserData();
            gerenciadorDeJanela = (GerenciadorDeJanela) params.get("gerenciador");
            buttonPesquisar();
            if (newValue != null) {
                newValue.windowProperty().addListener((ObservableValue<? extends Window> observable1, Window oldValue1, Window newValue1) -> {
                    stage = (Stage) newValue1;
                    Platform.runLater(() -> {
                        stage.setResizable(true);
                        stage.setMaximized(true);
                    });
                });
            }
        });

        filtroProjeto.getItems().setAll(new ProjetoDAO().pegarTodos());
        buttonPesquisar();
    }

    @FXML
    private void buttonLimpar() {
        filtroPacote.setValue(null);
        filtroProjeto.setValue(null);
        filtroModulo.setValue(null);
    }

    private void montarParametro() {

        String idProjeto;
        String idModulo;
        String idPacote;

        if (filtroProjeto.getValue() != null) {
            idProjeto = filtroProjeto.getValue().getId();
        } else {
            idProjeto = "";
        }

        if (filtroModulo.getValue() != null) {
            idModulo = filtroModulo.getValue().getId();
        } else {
            idModulo = "";
        }

        if (filtroPacote.getValue() != null) {
            idPacote = filtroPacote.getValue().getId();
        } else {
            idPacote = "";
        }

        params.put("pacote", idPacote);
        params.put("modulo", idModulo);
        params.put("projeto", idProjeto);
    }

    @FXML
    private void carregaModulos() {
        if (filtroProjeto.getValue() != null) {
            filtroModulo.getItems().setAll(new ModuloDAO().pegarPorProjeto(filtroProjeto.getValue()));
        } else {
            filtroModulo.getItems().clear();
        }
    }

    @FXML
    private void carregaPacotes() {
        if (filtroModulo.getValue() != null) {
            filtroPacote.getItems().setAll(new PacoteDAO().pegarPorModulo(filtroModulo.getValue()));
        } else {
            filtroPacote.getItems().clear();
        }
    }

    @FXML
    private void buttonPesquisar() {
        piLoader.setProgress(0d);
        piLoader.setVisible(true);
        apPrincipal.setDisable(true);
        new Thread(() -> {
            teste();
            Platform.runLater(() -> {
                piLoader.setProgress(1);
                apPrincipal.setDisable(false);
                piLoader.setVisible(false);
            });
        }).start();
    }

    @FXML
    private void cabecalhoMouseEvent(MouseEvent mouseEvent) {
        spContainer.setContent(apMeses);
        filtroProjeto.getItems().setAll(new ProjetoDAO().pegarTodos());
        buttonPesquisar();
    }

    @FXML
    private void miProjetosActionEvent(ActionEvent ae) {
        spContainer.setContent(gerenciadorDeJanela.carregarComponente("ManterProjetos"));
    }

    @FXML
    private void miModuloActionEvent(ActionEvent ae) {
        spContainer.setContent(gerenciadorDeJanela.carregarComponente("ManterModulo"));
    }

    @FXML
    private void miCadastrarUsuariosActionEvent(ActionEvent ae) {
        spContainer.setContent(gerenciadorDeJanela.carregarComponente("ManterUsuario"));
    }

    @FXML
    private void miPerfilActionEvent(ActionEvent ae) {
        spContainer.setContent(gerenciadorDeJanela.carregarComponente("ManterPerfil"));
    }

    @FXML
    private void miManterPacotesActionEvent(ActionEvent ae) {
        spContainer.setContent(gerenciadorDeJanela.carregarComponente("ManterPacote"));
    }

    @FXML
    private void miManterAtuacaoActionEvent(ActionEvent ae) {
        spContainer.setContent(gerenciadorDeJanela.carregarComponente("ManterAtuacao"));
    }

    @FXML
    private void miOrdemServicoActionEvent(ActionEvent ae) {
        spContainer.setContent(gerenciadorDeJanela.carregarComponente("ManterOrdemServico"));
    }

    @FXML
    private void miManterParametroActionEvent(ActionEvent ae) {
        spContainer.setContent(gerenciadorDeJanela.carregarComponente("ManterParametro"));
    }

    public void teste() {
        if (gerenciadorDeJanela != null) {
            Platform.runLater(() -> {
                gpMeses.getChildren().clear();
            });
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, spAno.getValue());
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int linha = 0;
            int coluna = 0;
            CustoDAO custoDao = new CustoDAO();
            while (calendar.get(Calendar.YEAR) <= spAno.getValue()) {
                Custo custo = custoDao.buscarCustoMes(calendar.getTime());
                params.put("Custo", custo);
                montarParametro();
                params.put("data", calendar.getTime());
                final int index = coluna + (linha * 3);
                final int col = coluna;
                final int row = linha;
                final Parent parent = gerenciadorDeJanela.carregarComponente("StatusMensalComponent" + index, params);
                Platform.runLater(() -> {
                    gpMeses.add(parent, col, row);
                    piLoader.setProgress(piLoader.getProgress() + 0.09);
                });
                calendar.add(Calendar.MONTH, 1);
                coluna++;
                if (coluna >= 3) {
                    coluna = 0;
                    linha++;
                }
            }
        }
    }

    private void visibilidadeMenu(boolean visible){
         mbGerenciar.setVisible(visible);
        menu.setVisible(visible);
        for (MenuItem item : menu.getItems()) {
            item.setVisible(visible);
        }
    }
    
    @Override
    public void buildAnalista() {
        visibilidadeMenu(false);
    }

    @Override
    public void buildBancoDados() {
        visibilidadeMenu(false);
    }

    @Override
    public void buildBDMG() {
        visibilidadeMenu(false);       
    }

    @Override
    public void buildDesenvolvedor() {
        visibilidadeMenu(false);
    }

    @Override
    public void buildGerente() {
        visibilidadeMenu(true);
    }

    @Override
    public void buildQualidade() {
       visibilidadeMenu(false);
    }


}
