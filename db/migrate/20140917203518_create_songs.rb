class CreateSongs < ActiveRecord::Migration
  def up
    create_table :songs do |t|
      t.string :name
      t.string :artist
      t.string :device_id
      t.string :username

      t.timestamps
    end

    add_index :songs, :device_id
  end

  def down
    drop_table :songs
  end
end
