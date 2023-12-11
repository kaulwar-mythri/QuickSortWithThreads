import java.util.*;
import java.util.concurrent.*;

class QuickSortCallable implements Callable {
    List<Integer> list;

    public QuickSortCallable(List<Integer> list) {
        this.list = list;
    }

    @Override
    public List<Integer> call() throws Exception {
        if(list.size() <= 1)
            return list;

        Integer pivot = list.get(list.size() - 1);
        List<Integer> less = new ArrayList<>();
        List<Integer> greater = new ArrayList<>();
        List<Integer> equal = new ArrayList<>();


        for(Integer x: list) {
            if(x < pivot)
                less.add(x);
            else if(x == pivot)
                equal.add(x);
            else
                greater.add(x);
        }

        QuickSortCallable lessTask = new QuickSortCallable(less);
        QuickSortCallable greaterTask = new QuickSortCallable(greater);

        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<List<Integer>> lessRes = pool.submit(lessTask);
        Future<List<Integer>> greaterRes = pool.submit(greaterTask);

        try {
            List<Integer> sortedList = new ArrayList<>();
            sortedList.addAll(lessRes.get());
            sortedList.addAll(equal);
            sortedList.addAll(greaterRes.get());

            return sortedList;
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }

        return list;
    }

}
public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Integer arr[] = {9, 8, 3, 6, 4, 1, 10, 5};
        List<Integer> list = new ArrayList<>(Arrays.asList(arr));

        QuickSortCallable callable = new QuickSortCallable(list);
        ExecutorService pool = Executors.newSingleThreadExecutor();
        Future<List<Integer>> ans = pool.submit(callable);

        System.out.println(ans.get());
        pool.shutdown();
    }
}