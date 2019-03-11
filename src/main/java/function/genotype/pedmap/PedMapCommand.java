package function.genotype.pedmap;

import java.util.Iterator;
import static utils.CommandManager.getValidFloat;
import static utils.CommandManager.getValidInteger;
import static utils.CommandManager.getValidPath;
import utils.CommandOption;

/**
 *
 * @author nick
 */
public class PedMapCommand {

    public static boolean isPedMap = false;
    public static boolean isEigenstrat = false;
    public static boolean isKinship = false;
    public static boolean isFlashPCA = false;
    public static String sampleCoverageSummaryPath = "";
    public static int kinshipSeed = 42;
    public static float kinshipRelatednessThreshold = 0.0884f;
    //for flashpca, plink outlier removal
    public static boolean isFlashPCAKeepOutliers = false;
    public static boolean isFlashPCANoPlots = false;
    public static int flashPCANumEvec = 10;
    public static int flashPCANumNeighbor = 5;//nearest neighbor for outlier detection
    public static float flashPCAzThresh = -3f;//Z value per nearest neghbor

    public static void initOptions(Iterator<CommandOption> iterator) {
        CommandOption option;
        while (iterator.hasNext()) {
            option = (CommandOption) iterator.next();
            switch (option.getName()) {
                case "--eigenstrat":
                    isEigenstrat = true;
                    break;
                case "--kinship":
                    isKinship = true;
                    break;
                //for flashpca    
                case "--flashpca":
                    isFlashPCA = true;
                    break;
                case "--flashpca-include-outlier":
                    isFlashPCAKeepOutliers = true;
                    break;
                case "--flashpca-num-eigvec":
                    flashPCANumEvec = getValidInteger(option);
                    break;
                case "--flashpca-num-nearest-neighbor":
                    flashPCANumNeighbor = getValidInteger(option);
                    break;
                case "--flashpca-no-plots":
                    isFlashPCANoPlots = true;
                case "--flashpca-z-score-thresh":
                    flashPCAzThresh = getValidFloat(option);
                    break;
                //flashpca args end   
                case "--sample-coverage-summary":
                    sampleCoverageSummaryPath = getValidPath(option);
                    break;
                case "--kinship-seed":
                    kinshipSeed = getValidInteger(option);
                    break;
                case "--kinship-relatedness-threshold":
                    kinshipRelatednessThreshold = getValidFloat(option);
                    break;
                default:
                    continue;
            }
            iterator.remove();
        }
    }
}
