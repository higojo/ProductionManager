/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.stefanini.control;

import br.com.stefanini.control.dao.ProgressoAtividadeDAO;
import br.com.stefanini.model.entity.Atividade;
import br.com.stefanini.model.entity.ProgressoAtividade;
import br.com.stefanini.model.enuns.Faturamento;
import br.com.stefanini.model.enuns.TipoAtividade;
import br.com.stefanini.model.util.MessageUtil;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author lucas
 */
public class FaturarAtividadeController implements Initializable {

    @FXML
    private AnchorPane apPrincipal;

    @FXML
    private Label lbProjeto;

    @FXML
    private Label lbPacote;

    @FXML
    private Label lbModulo;

    @FXML
    private Label lbAtividade;

    @FXML
    private CheckBox cbAnalise;

    @FXML
    private CheckBox cbDesenvolvimento;

    @FXML
    private CheckBox cbTesteHomologacao;

    private Stage stage;

    private Atividade atividade;

    private ProgressoAtividade paAnalise;
    private ProgressoAtividade paDesenvolvimento;
    private ProgressoAtividade paTesteHomologacao;

    private GerenciadorDeJanela gerenciadorDeJanela;

    Map<String, Object> params = new HashMap<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        apPrincipal.sceneProperty().addListener((ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) -> {
            if (newValue != null) {
                stage = (Stage) newValue.getWindow();
                params = (Map) apPrincipal.getUserData();
            }
        });
    }

    @FXML
    private void btCancelarActionEvent(ActionEvent ae) {
        stage.close();
    }

    @FXML
    private void btConfirmarActionEvent(ActionEvent ae) {
        //Enviando para faturamento todas as fases do sistema
        enviarParaFaturamento(cbAnalise, paAnalise);
        enviarParaFaturamento(cbDesenvolvimento, paDesenvolvimento);
        enviarParaFaturamento(cbTesteHomologacao, paTesteHomologacao);
        stage.close();
    }

    public void configurarCheckBox(CheckBox checkBox, ProgressoAtividade progressoAtividade, TipoAtividade tipoAtividade) {
        if (progressoAtividade == null) {
            checkBox.setSelected(false);
            checkBox.setDisable(true);
            checkBox.setText(tipoAtividade.toString());
        } else {
            checkBox.setText(tipoAtividade + " " + progressoAtividade.getId().getProgresso() + "%");
            checkBox.setSelected(progressoAtividade.getFaturamento() == Faturamento.FO);
            checkBox.setDisable(progressoAtividade.getFaturamento() != Faturamento.AF || progressoAtividade.getId().getProgresso() != 100d);
        }
    }

    private void enviarParaFaturamento(CheckBox checkBox, ProgressoAtividade progressoAtividade) {
        if (checkBox.isSelected()) {
            progressoAtividade.setFaturamento(Faturamento.EF);
            ProgressoAtividadeDAO.getInstance().editar(progressoAtividade);
            MessageUtil.messageInformation("A fase de " + progressoAtividade.getId().getTipoAtividade() + " foi enviado para faturamento!");
        }
    }

    private void load() {
        paAnalise =  ProgressoAtividadeDAO.getInstance().pegarPorAtividadeTipo(atividade, TipoAtividade.LE).stream().findFirst().orElse(null);
        paDesenvolvimento = ProgressoAtividadeDAO.getInstance().pegarPorAtividadeTipo(atividade, TipoAtividade.DE).stream().findFirst().orElse(null);
        paTesteHomologacao = ProgressoAtividadeDAO.getInstance().pegarPorAtividadeTipo(atividade, TipoAtividade.TE).stream().findFirst().orElse(null);
        configurarCheckBox(cbAnalise, paAnalise, TipoAtividade.LE);
        configurarCheckBox(cbDesenvolvimento, paDesenvolvimento, TipoAtividade.DE);
        configurarCheckBox(cbTesteHomologacao, paTesteHomologacao, TipoAtividade.TE);
        if (paAnalise != null) {
            cbAnalise.setSelected(paAnalise.getFaturamento() == Faturamento.FO);
        }
        lbProjeto.setText(atividade.getPacote().getModulo().getProjeto().getDescricao());
        lbAtividade.setText(atividade.getDescricao());
        lbModulo.setText(atividade.getPacote().getModulo().getDescricao());
        lbPacote.setText(atividade.getPacote().getDescricao());
    }

    public void teste() {
        params = (Map<String, Object>) apPrincipal.getUserData();
        atividade = (Atividade) params.get("Atividade");
        gerenciadorDeJanela = (GerenciadorDeJanela) params.get("gerenciador");
        stage = (Stage) params.get("modalStage");
        load();
        params.put("Atividade", new Atividade());
    }
}
