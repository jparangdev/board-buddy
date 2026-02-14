import {useCallback, useEffect, useRef, useState} from 'react';
import {Link} from 'react-router-dom';
import type {Group} from '@/types';
import {groupService} from '@/services';
import {useAuth} from '@/hooks/useAuth';
import {CreateGroupModal} from '@/components/CreateGroupModal';
import styles from './GroupListPage.module.css';

export function GroupListPage() {
  const [groups, setGroups] = useState<Group[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const { user } = useAuth();

  const dragItem = useRef<number | null>(null);
  const dragOverItem = useRef<number | null>(null);
  const [dragIdx, setDragIdx] = useState<number | null>(null);
  const [overIdx, setOverIdx] = useState<number | null>(null);

  const fetchGroups = async () => {
    try {
      const data = await groupService.getMyGroups();
      setGroups(data);
    } catch (error) {
      console.error('Failed to fetch groups:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchGroups();
  }, []);

  const handleGroupCreated = (newGroup: Group) => {
    setGroups([...groups, newGroup]);
    setShowCreateModal(false);
  };

  const handleDragStart = useCallback((index: number) => {
    dragItem.current = index;
    setDragIdx(index);
  }, []);

  const handleDragEnter = useCallback((index: number) => {
    dragOverItem.current = index;
    setOverIdx(index);
  }, []);

  const handleDragEnd = useCallback(async () => {
    if (dragItem.current === null || dragOverItem.current === null || dragItem.current === dragOverItem.current) {
      setDragIdx(null);
      setOverIdx(null);
      dragItem.current = null;
      dragOverItem.current = null;
      return;
    }

    const updated = [...groups];
    const [removed] = updated.splice(dragItem.current!, 1);
    updated.splice(dragOverItem.current!, 0, removed);
    
    // Optimistic UI update
    setGroups(updated);

    try {
      await groupService.updateOrder(updated.map(g => g.id));
    } catch (error) {
      console.error('Failed to update group order:', error);
      // Rollback on failure
      fetchGroups();
    } finally {
      setDragIdx(null);
      setOverIdx(null);
      dragItem.current = null;
      dragOverItem.current = null;
    }
  }, [groups]);

  if (isLoading) {
    return (
      <div className={styles.loading}>
        <span className={styles.loadingIcon}>&#x1F3B2;</span>
        <p>Rolling the dice...</p>
      </div>
    );
  }

  return (
    <div className="container">
      <div className={styles.header}>
        <div>
          <h1>My Game Groups</h1>
          <p className="text-muted">Manage your board game crews</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
          + Create Group
        </button>
      </div>

      {groups.length === 0 ? (
        <div className={styles.emptyState}>
          <span className={styles.emptyIcon}>&#x1F0CF;</span>
          <h2>No groups yet</h2>
          <p>Create your first game group and invite friends!</p>
          <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
            Create Your First Group
          </button>
        </div>
      ) : (
        <div className={styles.grid}>
          {groups.map((group, index) => (
            <div
              key={group.id}
              className={`${styles.cardWrapper} ${dragIdx === index ? styles.dragging : ''} ${overIdx === index && dragIdx !== index ? styles.dragOver : ''}`}
              draggable
              onDragStart={() => handleDragStart(index)}
              onDragEnter={() => handleDragEnter(index)}
              onDragOver={(e) => e.preventDefault()}
              onDragEnd={handleDragEnd}
            >
              <div className={styles.dragHandle}>&#x2630;</div>
              <Link to={`/groups/${group.id}`} className={styles.card}>
                <div className={styles.cardIcon}>&#x265F;</div>
                <h3>{group.name}</h3>
                <div className={styles.cardMeta}>
                  {group.ownerId === user?.id && (
                    <span className="badge badge-gold">Owner</span>
                  )}
                  <span className={styles.date}>
                    Created {new Date(group.createdAt).toLocaleDateString()}
                  </span>
                </div>
              </Link>
            </div>
          ))}
        </div>
      )}

      {showCreateModal && (
        <CreateGroupModal
          onClose={() => setShowCreateModal(false)}
          onCreated={handleGroupCreated}
        />
      )}
    </div>
  );
}
