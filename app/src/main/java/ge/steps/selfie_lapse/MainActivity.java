package ge.steps.selfie_lapse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageGridAdapter mAdapter;

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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
