package it.unisa.softwaredependability;

import it.unisa.softwaredependability.cli.CliParser;
import it.unisa.softwaredependability.cli.JobArgs;
import it.unisa.softwaredependability.pipeline.DatasetExtractionPipeline;
import it.unisa.softwaredependability.pipeline.RefactoringMiningPipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        JobArgs jobArgs = CliParser.parse(args);

        // uncomment this line to enable the GHTorrent pipeline
        //executeRepositoryExtractionPipeline();
        executeRefactoringCommitPipeline(jobArgs);
    }

    static void executeRepositoryExtractionPipeline() {
        DatasetExtractionPipeline pipeline = null;
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("projectSourceFile", "/Volumes/Elements/github_archive/github-2019-06-01/projects.csv");
            config.put("commitSourceFile", "/Volumes/Elements/github_archive/github-2019-06-01/commits.csv");
            config.put("spark.local.dir", "/Volumes/Elements/github_archive/spark_temp");
            config.put("spark.sql.warehouse.dir", "/Volumes/Elements/github_archive/spark_temp/warehouse");
            config.put("output.dir", "/Users/martinsteinhauer/Desktop/commitResult");
            config.put("master", "local[4]");
            pipeline = new DatasetExtractionPipeline(config);
            pipeline.execute();
        } catch(Exception e) {
            e.printStackTrace();
            if(pipeline != null) {
                pipeline.shutdown();
            }
        }

    }

    static void executeRefactoringCommitPipeline(JobArgs jobArgs) {
        RefactoringMiningPipeline pipeline = null;
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("output.dir.commits", jobArgs.getCommitsOutputDir().toString());
            config.put("output.dir.metrics", jobArgs.getMetricsOutputDir() == null ? UUID.randomUUID().toString() : jobArgs.getMetricsOutputDir().toString());
            config.put("repository.list", jobArgs.getInputFile().toString());
            config.put("mining.refactoring.only", jobArgs.isRefactoringMiningOnly());
            config.put("jobs.parallel", jobArgs.getParallelJobs());
            config.put("repos.parallel", jobArgs.getParallelRepos());
            config.put("github.user", jobArgs.getUsername());
            config.put("github.token", jobArgs.getToken());
            config.put("github.branch", jobArgs.getBranch());
            config.put("batch.size", jobArgs.getBatchSize());
            if(jobArgs.getDeployMode() == JobArgs.DeployMode.LOCAL) {
                config.put("spark.local.dir", "/Volumes/Elements/github_archive/spark_temp");
                config.put("spark.sql.warehouse.dir", "/Volumes/Elements/github_archive/spark_temp/warehouse");
            }
            pipeline = new RefactoringMiningPipeline(config);
            pipeline.execute();
        } catch(Exception e) {
            e.printStackTrace();
            pipeline.shutdown();
        }
    }
}
