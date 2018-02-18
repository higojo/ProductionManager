/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.stefanini.control;

import br.com.stefanini.control.dao.AtividadeDAO;
import br.com.stefanini.control.dao.ModuloDAO;
import br.com.stefanini.control.dao.PacoteDAO;
import br.com.stefanini.control.dao.ProjetoDAO;
import br.com.stefanini.model.entity.Atividade;
import br.com.stefanini.model.entity.Modulo;
import br.com.stefanini.model.entity.OrdemServico;
import br.com.stefanini.model.entity.Pacote;
import br.com.stefanini.model.entity.Projeto;
import br.com.stefanini.model.enuns.Faturamento;
import br.com.stefanini.model.enuns.SituacaoAtividade;
import br.com.stefanini.model.util.StringUtil;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author rkkitagawa
 */
public class PesquisarAtividadeController implements Initializable {

    @FXML
    private ScrollPane spContainer;
    @FXML
    private AnchorPane apPrincipal;
    private GerenciadorDeJanela gerenciadorDeJanela;
    private Stage stage;
    @FXML
    private Label lbPesquisa;

    private Date param;

    @FXML
    private ComboBox<Projeto> cbProjeto;

    @FXML
    private ComboBox<Modulo> cbModulo;

    @FXML
    private ComboBox<Pacote> cbPacote;

    @FXML
    private TextField txAtividade;

    @FXML
    private ComboBox<SituacaoAtividade> cbSituacao;

    @FXML
    private ComboBox<Faturamento> cbFaturamento;

    @FXML
    private TableView<Atividade> tvAtividade;

    @FXML
    private TableColumn<Atividade, String> colId;

    @FXML
    private TableColumn<Atividade, OrdemServico> colOs;

    @FXML
    private TableColumn<Atividade, String> colAtividade;

    @FXML
    private TableColumn<Atividade, String> colEstimada;

    @FXML
    private TableColumn<Atividade, String> colDetalhada;

    @FXML
    private TableColumn<Atividade, String> colLevantamento;

    @FXML
    private TableColumn<Atividade, String> colDesenvolvimento;

    @FXML
    private TableColumn<Atividade, String> colHomologacao;

    @FXML
    private TableColumn<Atividade, String> colAcoes;

    @FXML
    private Label lbTotalEstimada;

    @FXML
    private Label lbTotalDetalhada;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            stage = (Stage) apPrincipal.getScene().getWindow();
            param = (Date) apPrincipal.getUserData();
            lbPesquisa.setText(buildLabel(param));
        });
        gerenciadorDeJanela = new GerenciadorDeJanela();
        cbPacote.setValue(null);
        cbModulo.setValue(null);
        cbPacote.setValue(null);
        cbProjeto.getItems().setAll(new ProjetoDAO().pegarTodos());
        txAtividade.setText("");
        cbSituacao.getItems().setAll(SituacaoAtividade.values());
        cbFaturamento.getItems().setAll(Faturamento.values());

        tvAtividade.getItems().setAll(new AtividadeDAO().pegarTodos());

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOs.setCellValueFactory(new PropertyValueFactory<>("ordemServico"));
        colAtividade.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colEstimada.setCellValueFactory(new PropertyValueFactory<>("contagemEstimada"));
        colDetalhada.setCellValueFactory(new PropertyValueFactory<>("contagemDetalhada"));
    }

    @FXML
    private void btPesquisarAction() {
        List<Atividade> atividades = new AtividadeDAO().pegarPorAtividade(buildAtividadeFromfxml());
        tvAtividade.getItems().setAll(atividades);
        buildTotais(atividades);
    }

    private void buildTotais(List<Atividade> atividades) {
        Double countEstimada = 0.0;
        Double countDetalhada = 0.0;
        if (atividades != null) {
            for (Atividade atividade : atividades) {                
                countEstimada += atividade.getContagemEstimada();
                countDetalhada += atividade.getContagemDetalhada();
            }
        }
        lbTotalEstimada.setText(String.valueOf(countEstimada));
        lbTotalDetalhada.setText(String.valueOf(countDetalhada));

    }

    private Atividade buildAtividadeFromfxml() {
        Atividade ativ = new Atividade();

        if (cbPacote.getValue() != null) {
            ativ.setPacote(cbPacote.getValue());
        } else {
            ativ.setPacote(new Pacote());
        }

        if (cbModulo.getValue() != null) {
            ativ.getPacote().setModulo(cbModulo.getValue());
        } else {
            ativ.getPacote().setModulo(new Modulo());
        }

        if (cbProjeto.getValue() != null) {
            ativ.getPacote().getModulo().setProjeto(cbProjeto.getValue());
        } else {
            ativ.getPacote().getModulo().setProjeto(new Projeto());
        }
        
        if (StringUtil.isEmpty(txAtividade.getText())) {
            ativ.setDescricao(txAtividade.getText());
        }
        if (cbSituacao.getValue() != null) {
            ativ.setSituacaoAtividade(cbSituacao.getValue());
        }
        if (cbFaturamento.getValue() != null) {
            ativ.setFaturamento(cbFaturamento.getValue());
        }
        return ativ;
    }

    private String buildLabel(Date date) {
        StringBuilder sb = new StringBuilder("Atividade(s) de ");
        sb.append(new SimpleDateFormat("MM/YYYY").format(date));
        return sb.toString();
    }

    @FXML
    private void cbProjetoAction() {
        if (cbProjeto.getValue() != null) {
            cbModulo.getItems().setAll(new ModuloDAO().pegarPorProjeto(cbProjeto.getValue()));
        } else {
            cbModulo.getItems().clear();
        }
    }

    @FXML
    private void cbModuloAction() {
        if (cbModulo.getValue() != null) {
            cbPacote.getItems().setAll(new PacoteDAO().pegarPorModulo(cbModulo.getValue()));
        } else {
            cbPacote.getItems().clear();
        }
    }

    @FXML
    private void btAdicionarAction(){        
        gerenciadorDeJanela.mostrarJanela(stage, gerenciadorDeJanela.carregarComponente("ManterAtividade"), "Início").show();

    }
}
