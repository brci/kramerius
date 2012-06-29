package cz.incad.Kramerius.views;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import com.google.inject.Provider;

import cz.incad.kramerius.processes.BatchStates;
import cz.incad.kramerius.processes.LRDefinitionAction;
import cz.incad.kramerius.processes.LRProcess;
import cz.incad.kramerius.processes.LRProcessDefinition;
import cz.incad.kramerius.processes.LRProcessOffset;
import cz.incad.kramerius.processes.LRProcessOrdering;
import cz.incad.kramerius.processes.States;
import cz.incad.kramerius.processes.TypeOfOrdering;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.service.ResourceBundleService;
import cz.incad.kramerius.utils.conf.KConfiguration;

public class ProcessViewObject {

    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ProcessViewObject.class.getName());

    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy/dd/MM - HH:mm:ss");
    
    private LRProcess lrProcess;
    private LRProcessDefinition definition;
    private LRProcessOrdering ordering;
    private LRProcessOffset offset;
    private TypeOfOrdering typeOfOrdering;
    //private String lrUrl;
    private ResourceBundleService bundleService;
    private Locale locale;

    private List<ProcessViewObject> childProcesses = new ArrayList<ProcessViewObject>();
    
    public ProcessViewObject(LRProcess lrProcess, LRProcessDefinition definition, LRProcessOrdering ordering, LRProcessOffset offset, TypeOfOrdering typeOfOrdering,  ResourceBundleService service, Locale locale) {
        super();
        this.lrProcess = lrProcess;
        this.ordering = ordering;
        this.offset = offset;
        this.typeOfOrdering = typeOfOrdering;
        this.definition = definition;
        this.bundleService = service;
        this.locale = locale;
    }

    public String getPid() {
        return lrProcess.getPid();
    }

    public String getName() {
        try {
            String unnamed = bundleService.getResourceBundle("labels", locale).getString("administrator.processes.unnamedprocess");
            if (lrProcess.getProcessName() == null) {
                return unnamed + " <br> <span style='font-size:80%; font-style:italic'>" + lrProcess.getDescription() + "</span>";
            } else {
                return lrProcess.getProcessName() + " <br> <span style='font-size:80%; font-style:italic'>" + lrProcess.getDescription() + "</span>";
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return "";
        }
    }

    public String getProcessName() {
        return getName();
        // if (this.definition.getProcessOutputURL() != null) {
        // return
        // "<a href=\""+this.definition.getProcessOutputURL()+"?uuid="+this.lrProcess.getUUID()+"\" target=\"_blank\">"+getName()+"</a>";
        // } else {
        // return
        // "<a href=\"dialogs/_processes_logs.jsp?uuid="+this.lrProcess.getUUID()+"\" target=\"_blank\">"+getName()+"</a>";
        // }
    }

    public String getProcessState() {
        return lrProcess.getProcessState().getVal() + " " + lrProcess.getProcessState().name();
    }
    
    public String getBatchState() {
        BatchStates bState = lrProcess.getBatchState();
        if (!bState.equals(BatchStates.NO_BATCH)) {
            return lrProcess.getBatchState().getVal() +" "+lrProcess.getBatchState().name();
        } else return "";
    }

    public String getStart() {
        Date date = new Date(lrProcess.getStartTime());
        if (date.getTime() != 0) {
            return FORMAT.format(date);
        } else return "";
    }

    public String getPlanned() {
        Date date = new Date(lrProcess.getPlannedTime());
        if (date.getTime() != 0L) {
            return FORMAT.format(date);
        } else return "";
    }

    public boolean isMasterProcess() {
        return lrProcess.isMasterProcess();
    }
    
    public String getUUID() {
        return lrProcess.getUUID();
    }
    
    public String getTreeIcon() {
        if (lrProcess.isMasterProcess()) {
            return "<a href=\"javascript:processes.subprocesses('"+this.lrProcess.getUUID()+"');\"><img border=\"0\" src=\"img/nolines_plus.gif\" id='"+this.lrProcess.getUUID()+"_icon'></img></a>";
        } else {
            return "<img border=\"0\" src=\"img/empty.gif\"></img>";
        }
    }
    
    // function killAndRefresh(url,ordering, offset, size, type) {

    public String getKillURL() {


        try {
            
            if ((this.lrProcess.getProcessState().equals(States.RUNNING)) || (this.lrProcess.getProcessState().equals(States.PLANNED))) {
                String url = "lr?action=stop&uuid=" + this.lrProcess.getUUID();
                String fn = "showConfirmDialog(dictionary['administrator.processes.kill.process.confirm'], function() {" +
                        "processes.doActionAndRefresh('" + url + "','" + this.ordering.name() + "'," + this.offset.getOffset() + "," + this.offset.getSize() + ",'" + this.typeOfOrdering.name() + "');" +
                "});";
                
                String renderedAHREF = "<a href=\"javascript:"+fn+ "\">"
                        + bundleService.getResourceBundle("labels", locale).getString("administrator.processes.kill.process") + "</a>";
                return renderedAHREF;
                
            } else {
                return "<i>"+bundleService.getResourceBundle("labels", locale).getString("administrator.processes.kill.process")+"</i>" ;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return "";
        }
    }

    
    public String getDeleteURL() {
        try {

            boolean notRunning = States.notRunningState(this.lrProcess.getProcessState());
            boolean notInBatch = BatchStates.notRunningBatch(this.lrProcess.getBatchState());
            
            
            if (notRunning && notInBatch) {

                String url = "lr?action=delete&uuid=" + this.lrProcess.getUUID();
                String fn = "showConfirmDialog(dictionary['administrator.processes.delete.process.confirm'], function() {" +
                    "processes.doActionAndRefresh('" + url + "','" + this.ordering.name() + "'," + this.offset.getOffset() + "," + this.offset.getSize() + ",'" + this.typeOfOrdering.name() + "');" +
                "});";

                String renderedAHREF = "<a href=\"javascript:"+fn+ "\">"
                        + bundleService.getResourceBundle("labels", locale).getString("administrator.processes.delete.process") + "</a>";
                return renderedAHREF;
            } else {
                return "<i>"+bundleService.getResourceBundle("labels", locale).getString("administrator.processes.delete.process")+"</i>";
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return "";
        }
    }

    public String getLogsURLs() {
        return getActionAHREF(this.definition.getLogsAction());
    }
    
    public String[] getActionsURLs() {
        List<String> hrefs = new ArrayList<String>();
        List<LRDefinitionAction> actions = this.definition.getActions();
        for (int i = 0, ll = actions.size(); i < ll; i++) {
            hrefs.add(getActionAHREF(actions.get(i)));
        }
        return (String[]) hrefs.toArray(new String[hrefs.size()]);
    }

    
    public String getStartedBy() {
        StringBuilder builder = new StringBuilder();
        String firstName = lrProcess.getFirstname();
        String surName = lrProcess.getSurname();
        String loginname = lrProcess.getLoginname();
        builder.append(loginname).append(" (").append(firstName).append(" ").append(surName).append(")");
        return builder.toString();
    }


    private String getActionAHREF(LRDefinitionAction act) {
        try {
            String bundleKey = act.getResourceBundleKey();
            if (bundleKey != null) {
                return "<a href=\"" + act.getActionURL() + "?uuid=" + this.lrProcess.getUUID() + "\" target=\"_blank\">" + bundleService.getResourceBundle("labels", locale).getString(bundleKey) + "</a>";
            } else {
                LOGGER.info(" action '" + act.getName() + "' has no bundle key");
                return "<a href=\"" + act.getActionURL() + "?uuid=" + this.lrProcess.getUUID() + "\" target=\"_blank\">" + act.getName() + "</a>";
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return "";
        }
    }
    
    public void addChildProcess(ProcessViewObject pw) {
        this.childProcesses.add(pw);
    }
    
    public void removeChildProcess(ProcessViewObject pw) {
        this.childProcesses.remove(pw);
    }
    
    public List<ProcessViewObject> getChildProcesses() {
        return this.childProcesses;
    }
}
