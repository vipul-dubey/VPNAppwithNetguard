package inc.bitwise.vpnazure.Utility;

import android.os.Handler;
import android.os.Looper;

//import java.util.Observable;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.Subscription;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Vipuld on 16/09/2019.
 */

public class AsyncLoader {

    public interface SyncBlock
    {
        void run() throws Exception;
    }

    public interface CompletionBlock
    {
        void run(Exception exception);
    }



        /**
         * Perform operation on background thread and call completion callback on UI thread.
         *
         * @param syncBlock          block to run on background thread
         * @param completionCallback block to run on UI thread.
         * @param delayInSeconds     delay after which sync block should be executed.
         */
        public static Subscription load(final SyncBlock syncBlock, final CompletionBlock completionCallback, long delayInSeconds)
        {
            return Observable.create(new Observable.OnSubscribe<Exception>()
            {

                public void call(Subscriber<? super Exception> subscriber)
                {
                    if (syncBlock != null)
                    {
                        try
                        {
                            syncBlock.run();
                            subscriber.onCompleted();
                        } catch (Exception e)
                        {
                            Timber.e(e);
                            subscriber.onError(e);
                        }
                    }
                }
            }).delay(delayInSeconds, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Exception>()
            {
                @Override
                public void onCompleted()
                {
                    if (completionCallback != null)
                    {
                        completionCallback.run(null);
                    }
                }

                @Override
                public void onError(Throwable e)
                {
                    if (completionCallback != null)
                    {
                        completionCallback.run((Exception) e);
                    }
                }

                @Override
                public void onNext(Exception e)
                {

                }
            });
        }

        public static Subscription load(final SyncBlock syncBlock, final CompletionBlock completionCallback)
        {
            return load(syncBlock, completionCallback, 0);
        }

        public static Subscription  load(final SyncBlock syncBlock)
        {
            return load(syncBlock, null);
        }

        private static Handler handler;

        private static Handler getHandler()
        {
            if (handler == null)
            {
                handler = new Handler(Looper.getMainLooper());
            }
            return handler;
        }

        public static void loadOnUIThread(final CompletionBlock completionBlock)
        {
            getHandler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    completionBlock.run(null);
                }
            });
            load(null, completionBlock);
        }


}
