package function.genotype.pedmap;

import function.genotype.base.AnalysisBase4CalledVar;
import function.genotype.base.CalledVariant;
import function.variant.base.Output;
import function.genotype.base.Sample;
import global.Data;
import function.genotype.base.SampleManager;
import utils.CommonCommand;
import utils.ErrorManager;
import utils.LogManager;
import utils.ThirdPartyToolManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;

/**
 *
 * @author nick
 */
public class PedMapGenerator extends AnalysisBase4CalledVar {

    BufferedWriter bwPed = null;
    BufferedWriter bwMap = null;
    BufferedWriter bwTmpPed = null;

    private static final String PLINK_HOME = "/nfs/goldstein/goldsteinlab/software/sh/plink";
    private static final String CHIP2PCA2_HOME = "/nfs/goldstein/goldsteinlab/software/sh/chip2pca2";

    final String pedFile = CommonCommand.outputPath + "output.ped";
    final String mapFile = CommonCommand.outputPath + "output.map";
    final String tmpPedFile = CommonCommand.outputPath + "output_tmp.ped";

    final String chip2pcaDir = CommonCommand.realOutputPath
            + File.separator + "chip2pca";
    final String crDir = chip2pcaDir + File.separator
            + CommonCommand.outputDirName + "-cr";

    final String outputOpt = chip2pcaDir + File.separator
            + CommonCommand.outputDirName + ".opt";

    int qualifiedVariants = 0;

    @Override
    public void initOutput() {
        try {
            bwPed = new BufferedWriter(new FileWriter(pedFile));
            bwMap = new BufferedWriter(new FileWriter(mapFile));
            bwTmpPed = new BufferedWriter(new FileWriter(tmpPedFile));
        } catch (Exception ex) {
            ErrorManager.send(ex);
        }
    }

    @Override
    public void doOutput() {
    }

    @Override
    public void closeOutput() {
        try {
            bwPed.flush();
            bwPed.close();
            bwMap.flush();
            bwMap.close();
        } catch (Exception ex) {
            ErrorManager.send(ex);
        }
    }

    @Override
    public void doAfterCloseOutput() {
        if (PedMapCommand.isEigenstrat) {
            doEigesntrat();
        }
    }

    @Override
    public void beforeProcessDatabaseData() {
    }

    @Override
    public void afterProcessDatabaseData() {
        generatePedFile();
    }

    @Override
    public void processVariant(CalledVariant calledVar) {
        try {
            Output output = new Output(calledVar);
            output.countSampleGenoCov();
            output.calculate();

            if (output.isValid()) {
                doOutput(calledVar);
            }
        } catch (Exception ex) {
            ErrorManager.send(ex);
        }
    }

    private void doOutput(CalledVariant calledVar) {
        try {
            qualifiedVariants++;

            bwMap.write(calledVar.getRegion().getChrStr() + "\t"
                    + calledVar.getVariantIdStr() + "\t"
                    + "0\t"
                    + calledVar.getRegion().getStartPosition());
            bwMap.newLine();

            outputTempGeno(calledVar);
        } catch (Exception ex) {
            ErrorManager.send(ex);
        }
    }

    private void generatePedFile() {
        try {
            LogManager.writeAndPrint("Output the data to ped file now...");

            bwTmpPed.flush();
            bwTmpPed.close();
            File tmpFile = new File(tmpPedFile);
            RandomAccessFile raf = new RandomAccessFile(tmpFile, "r");

            long rowLen = 2 * SampleManager.getListSize() + 1L;

            for (int s = 0; s < SampleManager.getListSize(); s++) {
                Sample sample = SampleManager.getList().get(s);

                String name = sample.getName();

                if (PedMapCommand.isEigenstrat) {
                    name = String.valueOf(sample.getPrepId());
                }

                int pheno = (int) sample.getPheno() + 1;

                bwPed.write(sample.getFamilyId() + " "
                        + name + " "
                        + sample.getPaternalId() + " "
                        + sample.getMaternalId() + " "
                        + sample.getSex() + " "
                        + pheno);

                for (int i = 0; i < qualifiedVariants; i++) {
                    for (int j = 0; j < 2; j++) {
                        long pos = i * rowLen + 2 * s + j;
                        raf.seek(pos);
                        byte allele = raf.readByte();
                        bwPed.write(" " + String.valueOf((char) allele));
                    }
                }

                bwPed.newLine();
            }

            tmpFile.delete();
        } catch (Exception ex) {
            ErrorManager.send(ex);
        }
    }

    private void outputTempGeno(CalledVariant calledVar) throws Exception {
        for (int s = 0; s < SampleManager.getListSize(); s++) {
            int geno = calledVar.getGenotype(SampleManager.getList().get(s).getIndex());
            if (geno == 2) {
                if (calledVar.isSnv()) {
                    bwTmpPed.write(calledVar.getAllele() + calledVar.getAllele());
                } else {
                    if (calledVar.isDel()) {
                        bwTmpPed.write("DD");
                    } else {
                        bwTmpPed.write("II");
                    }
                }
            } else if (geno == 1) {
                if (calledVar.isSnv()) {
                    bwTmpPed.write(calledVar.getRefAllele() + calledVar.getAllele());
                } else {
                    bwTmpPed.write("ID");
                }
            } else if (geno == 0) {
                if (calledVar.isSnv()) {
                    bwTmpPed.write(calledVar.getRefAllele() + calledVar.getRefAllele());
                } else {
                    if (calledVar.isDel()) {
                        bwTmpPed.write("II");
                    } else {
                        bwTmpPed.write("DD");
                    }
                }
            } else if (geno == Data.NA) {
                bwTmpPed.write("00");
            } else {
                bwTmpPed.write("00");
                LogManager.writeAndPrint("Invalid genotype: " + geno
                        + " (Variant ID: " + calledVar.getVariantIdStr() + ")");
            }
        }

        bwTmpPed.newLine();
    }

    public void doEigesntrat() {
        initDir(chip2pcaDir);

        File dir = initDir(crDir);

        String cmd = "cp " + Data.EXAMPLE_OPT_PATH + " " + outputOpt;

        ThirdPartyToolManager.systemCall(new String[]{cmd});

        cmd = PLINK_HOME + " --file " + CommonCommand.outputPath + "output --recode12 "
                + "--out " + crDir + File.separator + dir.getName();

        ThirdPartyToolManager.systemCall((new String[]{cmd}));

        cmd = PLINK_HOME + " --file " + CommonCommand.outputPath + "output --make-bed "
                + "--out " + crDir + File.separator + dir.getName();

        ThirdPartyToolManager.systemCall((new String[]{cmd}));

        cmd = "cd " + chip2pcaDir + "; "
                + CHIP2PCA2_HOME + " " + CommonCommand.outputDirName + " snppca";

        ThirdPartyToolManager.systemCall(new String[]{"/bin/sh", "-c", cmd});
    }

    private File initDir(String path) {
        File dir = new File(path);

        if (dir.exists()) {
            purgeDirectory(dir);
        } else {
            dir.mkdir();
        }

        return dir;
    }

    private void purgeDirectory(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                purgeDirectory(file);
            }

            file.delete();
        }
    }

    @Override
    public String toString() {
        return "It is generating ped/map files...";
    }
}
