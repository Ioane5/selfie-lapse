package ge.steps.selfie_lapse;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageGridAdapter mAdapter;

    private enum FilterOrder {
        date, anger, contempt, disgust, fear, happiness, neutral, sadness, surprise
    }

    private FilterOrder mFilterOrder;

    private static void sortByOrder(List<Selfie> list, final FilterOrder orderType) {
        Collections.sort(list, new Comparator<Selfie>() {
            @Override
            public int compare(Selfie lhs, Selfie rhs) {
                if (orderType == FilterOrder.date) {
                    return (int) (lhs.getDate() - rhs.getDate());
                }
                if (lhs.getEmotion() == null) {
                    if (rhs.getEmotion() == null)
                        return 0; // same both null
                    else
                        return 1; // second one has , first is null
                } else if (rhs.getEmotion() == null) {
                    return -1;
                }
                Emotion lhsEmotion = lhs.getEmotion();
                Emotion rhsEmotion = rhs.getEmotion();
                // else compare just types.
                switch (orderType) {
                    case anger:
                        return (int) (lhsEmotion.getAnger() - rhsEmotion.getAnger());
                    case contempt:
                        return (int) (lhsEmotion.getContempt() - rhsEmotion.getContempt());
                    case disgust:
                        return (int) (lhsEmotion.getDisgust() - rhsEmotion.getDisgust());
                    case happiness:
                        return (int) (lhsEmotion.getHappiness() - rhsEmotion.getHappiness());
                    case neutral:
                        return (int) (lhsEmotion.getNeutral() - rhsEmotion.getNeutral());
                    case fear:
                        return (int) (lhsEmotion.getFear() - rhsEmotion.getFear());
                    case sadness:
                        return (int) (lhsEmotion.getSadness() - rhsEmotion.getSadness());
                    case surprise:
                        return (int) (lhsEmotion.getSurprise() - rhsEmotion.getSurprise());
                }
                return 0;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // adapter & gallery stuff
        mAdapter = new ImageGridAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(mAdapter);
        FileStorage storage = FileStorage.getSelfieStore(getApplicationContext());
        mFilterOrder = FilterOrder.date; // default order type
        mAdapter.setData(storage.getAllSelfies());
    }

    class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageVH> {

        private List<Selfie> mData;

        class ImageVH extends RecyclerView.ViewHolder {
            ImageView image;

            public ImageVH(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
            }
        }

        public void setData(List<Selfie> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public ImageVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ImageVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.selfie_view, null, false));
        }

        @Override
        public void onBindViewHolder(final ImageVH holder, int position) {
            final Selfie selfie = mData.get(position);
            holder.image.setTag(selfie.getPath());
            holder.image.setImageResource(R.drawable.ic_image_placeholder);
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    File image = new File(selfie.getPath());
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inSampleSize = 4;
                    return BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                }

                @Override
                protected void onPostExecute(final Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    if (TextUtils.equals((String) holder.image.getTag(), selfie.getPath())) {
                        holder.image.animate().alpha(0).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                holder.image.setImageBitmap(bitmap);
                                holder.image.animate().alpha(1);
                            }
                        });
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_order:
                showOrderDialog();
                return true;
            case R.id.action_show_graph:
                // todo show graph activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View orderByDialog = LayoutInflater.from(this).inflate(R.layout.order_by_dialog, null, false);
        builder.setView(orderByDialog);

        builder.setPositiveButton(R.string.button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
    }
}
