package ge.steps.selfie_lapse;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageGridAdapter mAdapter;

    private List<Selfie> allSelfies;

    private ImageOrder mImageOrder;

    private enum ImageOrder {
        date, anger, contempt, disgust, fear, happiness, neutral, sadness, surprise
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
        allSelfies = storage.getAllSelfies();
        mImageOrder = ImageOrder.date; // default order type
        sortByOrder(allSelfies, mImageOrder);
        for (Selfie s : allSelfies)
            Log.d(TAG, "BLIAD " + s.toString());
        mAdapter.setData(new ArrayList<>(allSelfies));

        findViewById(R.id.fab_create_timelapse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
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
                startActivity(new Intent(getBaseContext(), EmotionGraphActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static int orderTypeToRadioButtonId(ImageOrder type) {
        switch (type) {
            case date:
                return R.id.date;
            case anger:
                return R.id.anger;
            case contempt:
                return R.id.contempt;
            case disgust:
                return R.id.disgust;
            case happiness:
                return R.id.happiness;
            case neutral:
                return R.id.neutral;
            case fear:
                return R.id.fear;
            case sadness:
                return R.id.sadness;
            case surprise:
                return R.id.surprise;
        }
        Log.e(TAG, "some shit happened");
        return R.id.date;
    }

    public static ImageOrder idToOrderType(int id) {
        switch (id) {
            case R.id.date:
                return ImageOrder.date;
            case R.id.anger:
                return ImageOrder.anger;
            case R.id.contempt:
                return ImageOrder.contempt;
            case R.id.disgust:
                return ImageOrder.disgust;
            case R.id.happiness:
                return ImageOrder.happiness;
            case R.id.neutral:
                return ImageOrder.neutral;
            case R.id.fear:
                return ImageOrder.fear;
            case R.id.sadness:
                return ImageOrder.sadness;
            case R.id.surprise:
                return ImageOrder.surprise;
            default:
                Log.e(TAG, "some shit happened 2");
                return ImageOrder.date;
        }
    }

    public void showOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View orderByDialog = LayoutInflater.from(this).inflate(R.layout.order_by_dialog, null, false);
        builder.setView(orderByDialog);
        final RadioGroup group = (RadioGroup) orderByDialog.findViewById(R.id.radio_group);
        int checkedId = orderTypeToRadioButtonId(mImageOrder);
        group.check(checkedId);

        builder.setPositiveButton(R.string.button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mImageOrder = idToOrderType(group.getCheckedRadioButtonId());
                sortByOrder(allSelfies, mImageOrder);
                mAdapter.moveItems(allSelfies);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
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
                    bmOptions.inSampleSize = 2;
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

        public void moveItem(int fromPosition, int toPosition) {
            final Selfie model = mData.remove(fromPosition);
            mData.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }

        public void moveItems(List<Selfie> newOrder) {
            for (int toPosition = newOrder.size() - 1; toPosition >= 0; toPosition--) {
                final Selfie model = newOrder.get(toPosition);
                final int fromPosition = mData.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }
    }

    private static void sortByOrder(List<Selfie> list, final ImageOrder orderType) {
        Collections.sort(list, new Comparator<Selfie>() {
            @Override
            public int compare(Selfie lhs, Selfie rhs) {
                if (orderType == ImageOrder.date) {
                    return (int) (rhs.getDate() - lhs.getDate());
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
                double diff = 0;
                switch (orderType) {
                    case anger:
                        diff = (rhsEmotion.getAnger() - lhsEmotion.getAnger());
                        break;
                    case contempt:
                        diff = (rhsEmotion.getContempt() - lhsEmotion.getContempt());
                        break;
                    case disgust:
                        diff = (rhsEmotion.getDisgust() - lhsEmotion.getDisgust());
                        break;
                    case happiness:
                        diff = (rhsEmotion.getHappiness() - lhsEmotion.getHappiness());
                        break;
                    case neutral:
                        diff = (rhsEmotion.getNeutral() - lhsEmotion.getNeutral());
                        break;
                    case fear:
                        diff = (rhsEmotion.getFear() - lhsEmotion.getFear());
                        break;
                    case sadness:
                        diff = (rhsEmotion.getSadness() - lhsEmotion.getSadness());
                        break;
                    case surprise:
                        diff = (rhsEmotion.getSurprise() - lhsEmotion.getSurprise());
                        break;

                }
                if (diff < 0) return -1;
                if (diff == 0) return 0;
                return 1;
            }
        });
    }

}
