package csc435.app;

public class CleanDataset
{
    public long dataset_size = 0;
    public double execution_time = 0.0;

    public void clean_dataset(String input_dir, String output_dir)
    {
        // TO-DO implement clean dataset logic
    }

    public static void main(String[] args)
    {
        if (args.length != 2) {
            System.err.println("improper number of arguments");
            System.exit(1);
        }

        CleanDataset cleanDataset = new CleanDataset();

        cleanDataset.clean_dataset(args[0], args[1]);
        
        System.out.print("Finished cleaning " + cleanDataset.dataset_size + " MiB of data");
        System.out.println(" in " + cleanDataset.execution_time + " miliseconds");
    }
}