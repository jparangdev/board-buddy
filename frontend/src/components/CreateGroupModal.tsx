import {type FormEvent, useEffect, useState} from 'react';
import type {ApiError, Group, User} from '@/types';
import {groupService, userService} from '@/services';
import {useAuth, useDebounce} from '@/hooks';
import styles from './Modal.module.css';

interface Props {
  onClose: () => void;
  onCreated: (group: Group) => void;
}

export function CreateGroupModal({ onClose, onCreated }: Props) {
  const [name, setName] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  // Member search state
  const [keyword, setKeyword] = useState('');
  const [searchResults, setSearchResults] = useState<User[]>([]);
  const [selectedMembers, setSelectedMembers] = useState<User[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const { user: currentUser } = useAuth();

  const debouncedKeyword = useDebounce(keyword, 300);

  useEffect(() => {
    if (!debouncedKeyword.trim()) {
      setSearchResults([]);
      return;
    }

    const search = async () => {
      setIsSearching(true);
      try {
        const users = await userService.searchUsers(debouncedKeyword.trim());
        const selectedIds = new Set(selectedMembers.map((m) => m.id));
        const filtered = users.filter(
          (u) => u.id !== currentUser?.id && !selectedIds.has(u.id)
        );
        setSearchResults(filtered);
      } catch {
        setSearchResults([]);
      } finally {
        setIsSearching(false);
      }
    };
    search();
  }, [debouncedKeyword, currentUser?.id, selectedMembers]);

  const addMember = (user: User) => {
    setSelectedMembers((prev) => [...prev, user]);
    setSearchResults((prev) => prev.filter((u) => u.id !== user.id));
    setKeyword('');
  };

  const removeMember = (userId: number) => {
    setSelectedMembers((prev) => prev.filter((m) => m.id !== userId));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const memberIds = selectedMembers.map((m) => m.id);
      const group = await groupService.create(name, memberIds);
      onCreated(group);
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || 'Failed to create group');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.header}>
          <h2>&#x1F3B2; Create New Group</h2>
          <button className={styles.closeBtn} onClick={onClose}>
            &times;
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="groupName">Group Name</label>
            <input
              id="groupName"
              type="text"
              className="input"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="e.g., Friday Night Gamers"
              maxLength={100}
              required
            />
          </div>

          <div className="form-group" style={{ marginTop: 'var(--spacing-md)' }}>
            <label htmlFor="memberSearch">Add Members</label>
            <input
              id="memberSearch"
              type="text"
              className="input"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="Search by nickname..."
            />
            <p className={styles.searchHint}>
              You are automatically added as the owner
            </p>
          </div>

          {isSearching && (
            <p className={styles.noResults}>Searching...</p>
          )}

          {!isSearching && debouncedKeyword.trim() && searchResults.length === 0 && (
            <p className={styles.noResults}>No users found</p>
          )}

          {searchResults.length > 0 && (
            <div className={styles.searchResults}>
              {searchResults.map((user) => (
                <div
                  key={user.id}
                  className={styles.userCard}
                  onClick={() => addMember(user)}
                >
                  <div className={styles.userCardAvatar}>
                    {user.nickname.charAt(0).toUpperCase()}
                  </div>
                  <div className={styles.userCardInfo}>
                    <span className={styles.userCardName}>{user.nickname}</span>
                    <span className={styles.userCardTag}>{user.userTag}</span>
                  </div>
                </div>
              ))}
            </div>
          )}

          {selectedMembers.length > 0 && (
            <div className={styles.chipList}>
              {selectedMembers.map((member) => (
                <span key={member.id} className={styles.chip}>
                  {member.nickname}
                  <button
                    type="button"
                    className={styles.chipRemove}
                    onClick={() => removeMember(member.id)}
                  >
                    &times;
                  </button>
                </span>
              ))}
            </div>
          )}

          {error && <p className={styles.error}>{error}</p>}

          <div className={styles.actions}>
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={isLoading}>
              {isLoading ? 'Creating...' : 'Create Group'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
