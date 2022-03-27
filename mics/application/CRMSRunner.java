package bgu.spl.mics.application;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static CountDownLatch CDL;

    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Student> STUDENTS = new ArrayList<>();
        ArrayList<ConfrenceInformation> CONFERENCE = new ArrayList<>();
        ArrayList<GPU> GPUS = new ArrayList<>();
        ArrayList<CPU> CPUS = new ArrayList<>();
        int Duration = 0;
        int speed = 0;

        /** read input file */
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Reader reader = new FileReader("example_input.json")) {
            JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
            String jsonInputString = gson.toJson(jsonElement);
            Input input = new Gson().fromJson(jsonInputString, Input.class);
            for (Student mystud : input.getStudents()) {
                if (mystud.getStatus().equals("MSc")) {
                    mystud.setDegree(Student.Degree.MSc);
                } else if (mystud.getStatus().equals("PhD")) {
                    mystud.setDegree(Student.Degree.PhD);
                }
                ConcurrentLinkedQueue<Model> models = mystud.getModels();
                for (Model mod : models) {
                    String t = mod.getType();
                    int size = mod.getSize();
                    Data.Type dType = Data.Type.Images;
                    if (t == "Images") {
                        dType = Data.Type.Images;
                    } else if (t == "Text") {
                        dType = Data.Type.Text;
                    } else if (t == "Tabular") {
                        dType = Data.Type.Tabular;
                    }
                    mod.setData(new Data(dType, size));
                    mod.setStatus(Model.status.PreTrained);
                    mod.setResults(Model.results.None);
                    mod.setStudent(mystud);
                }
                STUDENTS.add(mystud);
            }

            for (String gpu : input.getGpus()) {
                if (gpu.equals("RTX3090")) {
                    GPUS.add(new GPU(GPU.Type.RTX3090));
                } else if (gpu.equals("RTX2080")) {
                    GPUS.add(new GPU(GPU.Type.RTX2080));
                } else {
                    GPUS.add(new GPU(GPU.Type.GTX1080));
                }
            }
            for (Integer i : input.getCpus()) {
                CPUS.add(new CPU(i));
            }

            for (ConfrenceInformation conf : input.getConf()) {
                conf.setModels(new LinkedBlockingQueue<>());
                CONFERENCE.add(conf);
            }

            speed = input.getTickTime();
            Duration = input.getDuration();

        } catch (IOException e) {}

        /** create Micro-service for each Object */
        List<StudentService> studentServices = new ArrayList<>();
        List<GPUService> gpuServices = new ArrayList<>();
        List<CPUService> cpuServices = new ArrayList<>();
        List<ConferenceService> conferenceServices = new ArrayList<>();
        int Counter = 0;

        for (int i = 0; i < STUDENTS.size(); i++) {
            studentServices.add(new StudentService("Student" + i, STUDENTS.get(i)));
        }
        for (int i = 0; i < GPUS.size(); i++) {
            gpuServices.add(new GPUService("GPU" + i, GPUS.get(i)));
            Counter++;
        }
        for (int i = 0; i < CPUS.size(); i++) {
            cpuServices.add(new CPUService("CPU" + i, CPUS.get(i)));
            Counter++;
        }
        for (int i = 0; i < CONFERENCE.size(); i++) {
            conferenceServices.add(new ConferenceService("ConferenceService" + i, CONFERENCE.get(i)));
            Counter++;
        }

        TimeService timeService = new TimeService(speed, Duration);
        CDL = new CountDownLatch(Counter);

        /** create threads for each Micro-service and save them in lists */
        List<Thread> studentThreads = new ArrayList<>();
        List<Thread> GPUThreads = new ArrayList<>();
        List<Thread> CPUThreads = new ArrayList<>();
        List<Thread> conferenceThreads = new ArrayList<>();

        for (int i = 0; i < studentServices.size(); i++) {
            studentThreads.add(new Thread(studentServices.get(i)));
        }

        for (int i = 0; i < gpuServices.size(); i++) {
            GPUThreads.add(new Thread(gpuServices.get(i)));
        }

        for (int i = 0; i < cpuServices.size(); i++) {
            CPUThreads.add(new Thread(cpuServices.get(i)));
        }

        for (int i = 0; i < conferenceServices.size(); i++) {
            conferenceThreads.add(new Thread(conferenceServices.get(i)));
        }

        Thread time = new Thread(timeService);

        /** start threads */

        for (int i = 0; i < GPUThreads.size(); i++) {
            GPUThreads.get(i).start();
        }
        for (int i = 0; i < CPUThreads.size(); i++) {
            CPUThreads.get(i).start();
        }
        for (int i = 0; i < conferenceThreads.size(); i++) {
            conferenceThreads.get(i).start();
        }

        try {
            CDL.await();
        } catch (InterruptedException e) {
        }

        for (int i = 0; i < studentThreads.size(); i++) {
            studentThreads.get(i).start();
        }

        time.start();

        try {
            Thread.sleep(Duration);
            time.join();
        }catch (InterruptedException e){}

            /** create output file*/

            Cluster cluster = Cluster.getInstance();
            String output = "{" + '\n' +
                    "Students:[" + '\n';
            for (Student student : STUDENTS) {
                output = output + studentToString(student) + '\n';
            }
            output += "]" + '\n' +
                    "}" + '\n';
            output += "Conferences:[" + '\n';
            for (ConfrenceInformation conf : CONFERENCE) {
                output =output + conferenceToString(conf) + '\n';
            }

            output +="cpuTimeUsed:"+ cluster.getTotalCpuTime().intValue()+'\n';
            output +="gpuTimeUsed:"+ cluster.getTotalGpuTime().intValue()+'\n';
            output +="batchesProcessed:"+ cluster.getTotalBatcesProcessed().intValue()+'\n'+
                    "}";


            try {
                FileWriter writer = new FileWriter("output.txt");
                writer.write(output);
                writer.close();
                System.out.println("file created");

            } catch (IOException e) {}

            System.exit(0);
    }

    public static String studentToString(Student s) {
        Cluster cluster = Cluster.getInstance();
        String out = "name: " + s.getName() + '\n'+
                ", department: " + s.getDepartment() + '\n' +
                ", status: " + s.getStatus() +'\n'+
                ", publications: " + s.getPublications() +'\n'+
                ", papersRead: " + s.getPapersRead() +'\n'+
                ", trainedModels: "+"["+'\n'+"{"+trainedModelsToString(s,cluster)+"}"+
                '}';
        return out;
    }

    public static String trainedModelsToString(Student student, Cluster cluster){

        String out="";
        for(Model m: cluster.getModelsTrained())
            if (m.getStudent()==student){
                out = out + modelToString(m);
            }

        return out;
    }


    public static String modelToString(Model model) {
        String out =  '\t' + "{" + '\n'+
                '\t'+'\t'+"name:'" + model.getName() + '\n' +
                '\t'+'\t'+"data:" + dataToString(model.getData()) +'\n'+
                '\t'+'\t'+"status:" + model.getStatus() +'\n'+
                '\t'+'\t'+"result:" + model.getResults() +'\n'+
                '\t'+"}"+'\n';
        return out;
    }


    public static String dataToString(Data data) {
        return ":{" +'\n'+
                "            " + "type: " + data.getType() +'\n'+
                "            " + "size: " + data.getSize() +'\n'+
                "       " + "}";
    }

    public static String conferenceToString(ConfrenceInformation c){
        String out = '\t' + "{" + '\n'+
                '\t'+'\t'+"name:'" + c.getName() + '\n' +
                '\t'+'\t'+"data:" + c.getDate() +'\n'+
                '\t'+'\t'+"publications:" + publicationsToString(c) +'\n'+
                '\t'+"}"+'\n';
        return out;
    }

    public static String publicationsToString(ConfrenceInformation confi){
        String out="";
        for(Model m:confi.getModels())
            out+=m.toString();
        return out;
    }


}
